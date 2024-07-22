package br.com.jbst.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.jbst.DTO.AtualizarDadosRequestDto;
import br.com.jbst.DTO.AtualizarDadosResponseDto;
import br.com.jbst.DTO.AutenticarRequestDto;
import br.com.jbst.DTO.AutenticarResponseDto;
import br.com.jbst.DTO.CriarContaRequestDto;
import br.com.jbst.DTO.CriarContaResponseDto;
import br.com.jbst.DTO.MailSenderDto;
import br.com.jbst.DTO.PerfilDto;
import br.com.jbst.DTO.RecuperarSenhaRequestDto;
import br.com.jbst.DTO.RecuperarSenhaResponseDto;
import br.com.jbst.components.MD5Component;
import br.com.jbst.components.MailSenderComponent;
import br.com.jbst.components.TokenCreatorComponent;
import br.com.jbst.entities.Perfil;
import br.com.jbst.entities.Usuario;
import br.com.jbst.producers.UsuarioMessageProducer;
import br.com.jbst.repositories.PerfilRepository;
import br.com.jbst.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsuarioService {
	private static final PerfilDto PerfilDto = null;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private MD5Component md5Component;

	@Autowired
	private TokenCreatorComponent tokenCreatorComponent;

	@Autowired
	private UsuarioMessageProducer usuarioMessageProducer;

	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ModelMapper modelMapper;

	public AutenticarResponseDto autenticar(AutenticarRequestDto dto) throws Exception {
		// Verificar se o usuário existe no banco de dados
		Usuario usuario = usuarioRepository.findByEmailAndSenha(dto.getEmail(), md5Component.encrypt(dto.getSenha()));

		// Verificar se o usuário não foi encontrado
		if (usuario == null) {
			throw new IllegalArgumentException("Acesso negado. Usuário não encontrado.");
		}

		// Verificar se o ID do perfil fornecido no DTO corresponde ao perfil associado
		// ao usuário
		if (dto.getId() != null && !usuario.getPerfil().getId().equals(dto.getId())) {
			throw new IllegalArgumentException("Acesso negado. ID de perfil inválido.");
		}

		// Gerar os dados de autenticação do usuário.
		AutenticarResponseDto response = new AutenticarResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setAccessToken(tokenCreatorComponent.generateToken(usuario.getId().toString()));
		response.setDataHoraAcesso(Instant.now());
		response.setDataHoraExpiracao(Instant.now()); // TODO implementar a expiração do token

		// Definir o perfil no DTO de resposta
		Perfil perfil = usuario.getPerfil();
		if (perfil != null) {
			PerfilDto perfilDto = new PerfilDto();
			perfilDto.setId(perfil.getId());
			perfilDto.setNome(perfil.getNome());
			response.setPerfil(perfilDto);
		}

		return response;
	}

	public ResponseEntity<CriarContaResponseDto> criarConta(CriarContaRequestDto dto) throws Exception {
		validarEmailNaoCadastrado(dto.getEmail());
		try {
			Perfil perfil = buscarPerfilPeloId(dto);

			Usuario usuario = criarUsuario(dto, perfil);

//	    enviarMensagemAssincrona(usuario);
			MailSenderDto mailSenderDto = criarMessage(usuario);

			// gravando os dados na fila do RabbitMQ
			usuarioMessageProducer.sendMessage(objectMapper.writeValueAsString(mailSenderDto));

			// Envie o e-mail real usando o MailSenderComponent
			mailSenderComponent.sendMessage(mailSenderDto);

			Usuario novoUsuario = salvarUsuario(usuario);
			log.info("Cadastrado com sucesso: " + usuario.getNome());
			CriarContaResponseDto response = criarResponseDto(novoUsuario, perfil);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			log.error("Erro ao criar conta de usuário", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	private void validarEmailNaoCadastrado(String email) {
		if (usuarioRepository.findByEmail(email) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O email informado já está cadastrado. Tente outro.");
		}
	}

	private Perfil buscarPerfilPeloId(CriarContaRequestDto dto) {
		return perfilRepository.findById(dto.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Perfil não encontrado com o ID fornecido: " + dto.getId()));
	}

	private Usuario criarUsuario(CriarContaRequestDto dto, Perfil perfil) {
		Usuario usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(md5Component.encrypt(dto.getSenha()));
		usuario.setDataHoraCriacao(Instant.now());
		usuario.setDataHoraAlteracao(Instant.now());
		usuario.setPerfil(perfil);
		return usuario;
	}

	private void enviarMensagemAssincrona(Usuario usuario) throws Exception {
		MailSenderDto mailSenderDto = criarMessage(usuario);
		usuarioMessageProducer.sendMessage(objectMapper.writeValueAsString(mailSenderDto));
		mailSenderComponent.sendMessage(mailSenderDto);
	}

	private Usuario salvarUsuario(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	private CriarContaResponseDto criarResponseDto(Usuario usuario, Perfil perfil) {
		CriarContaResponseDto response = new CriarContaResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraCriacao(usuario.getDataHoraCriacao());
		response.setPerfil(new PerfilDto(perfil.getId(), perfil.getNome()));
		return response;
	}

	public RecuperarSenhaResponseDto recuperarSenha(RecuperarSenhaRequestDto dto) throws Exception {
		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail());

		// verificando se o usuário não foi encontrado
		if (usuario == null)
			throw new IllegalArgumentException("Usuário não encontrado. Verifique o email informado.");

		// gerando uma nova senha para o usuário
		Faker faker = new Faker();
		String novaSenha = faker.internet().password(8, 10, true, true, true);

		MailSenderDto mailSenderDto = recuperarMessage(usuario, novaSenha);

		// gravando os dados na fila do RabbitMQ
		usuarioMessageProducer.sendMessage(objectMapper.writeValueAsString(mailSenderDto));

		// Envie o e-mail real usando o MailSenderComponent
		mailSenderComponent.sendMessage(mailSenderDto);

		// atualizando os dados do usuário no banco de dados
		usuario.setSenha(md5Component.encrypt(novaSenha));
		usuarioRepository.save(usuario);

		// retornando a resposta da requisição
		RecuperarSenhaResponseDto response = new RecuperarSenhaResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraRecuperacaoDeSenha(Instant.now());

		return response;
	}

	public AtualizarDadosResponseDto atualizarDados(AtualizarDadosRequestDto dto, String accessToken) throws Exception {
		// extrair o id do usuário gravado no token
		String id = tokenCreatorComponent.getIdFromToken(accessToken);
		Usuario registro = usuarioRepository.findById(UUID.fromString(id)).get();

		// buscando o usuário no banco de dados através do ID
		Optional<Usuario> optional = usuarioRepository.findById(dto.getId());
		// verificando se nenhum usuário foi encontrado
		if (optional.isEmpty() || !optional.get().getEmail().equals(registro.getEmail()))
			throw new IllegalArgumentException("Usuário não encontrado. Verifique o id informado.");

		// capturar o usuário obtido do banco de dados
		Usuario usuario = optional.get();

		boolean isUpdated = false;

		// verificando se o nome está preenchido
		if (dto.getNome() != null && dto.getNome() != "") {
			usuario.setNome(dto.getNome());
			isUpdated = true;
		}

		// verificando se as senhas estão preenchidas
		if (dto.getSenhaAtual() != null && dto.getNovaSenha() != null) {
			// verificando se a senha atual está incorreta
			if (!usuario.getSenha().equals(md5Component.encrypt(dto.getSenhaAtual())))
				throw new IllegalArgumentException("Senha atual inválida.");
			else {
				usuario.setSenha(md5Component.encrypt(dto.getNovaSenha()));
				isUpdated = true;
			}
		}
		// verificando se algum campo do usuário foi modificado
		if (isUpdated) {
			// atualizando no banco de dados
			usuario.setDataHoraAlteracao(Instant.now());
			usuarioRepository.save(usuario);

			AtualizarDadosResponseDto response = new AtualizarDadosResponseDto();
			response.setId(usuario.getId());
			response.setNome(usuario.getNome());
			response.setEmail(usuario.getEmail());
			response.setDataHoraAlteracao(usuario.getDataHoraAlteracao());

			return response;
		} else {
			throw new IllegalArgumentException("Nenhum dado do usuário foi alterado.");
		}
	}

	public List<PerfilDto> ConsultarTodosOsPerfis(String nome) throws Exception {
		List<Perfil> perfil = perfilRepository.findAll();
		return modelMapper.map(perfil, new TypeToken<List<PerfilDto>>() {
		}.getType());
	}

	public List<CriarContaResponseDto> ConsultarTodosOsUsuarios(String nome) throws Exception {
		List<Usuario> usuario = usuarioRepository.findAll();
		return modelMapper.map(usuario, new TypeToken<List<CriarContaResponseDto>>() {
		}.getType());
	}

	private MailSenderDto criarMessage(Usuario usuario) {
		MailSenderDto mailSenderDto = new MailSenderDto();
		mailSenderDto.setMailTo(usuario.getEmail());
		mailSenderDto.setSubject("Criação de conta de usuário realizada com sucesso - JB Segurança do Trabalho");
		mailSenderDto.setBody(

				"<div>" + "<h2>Parabéns " + usuario.getNome() + "!</h2>"
						+ "<div>Sua conta de usuário foi criada com sucesso!</div></div>"
						+ "<div>Nosso diferencial é que todos os certificados, "
						+ "listas de presença, autorizações, proficiências de instrutores"
			 + " são digitalizados aqui em nosso Sistema! </div></div>"
			 + "<div>Criamos um processo rápido"
						+ " que agiliza a sua Rotina na Segurança do Trabalho. "
						+ "O melhor da sua Conta é que você não paga nada. </div></div>"
						+ "<div>Este sistema também gera de forma gratuita "
						+ "as Fichas de Epi e Ordens de Serviços dos Seus Colaboradores.</div></div> "
						+ "</div>Ele também gera para a sua empresa a: "
						+ " </div> -> APR - Análise Pre - Liminar de Riscos; "
						+ "</div> -> PT - Permissão do Trabalho; "
						+ "</div>-> PTA - Permissão de Trabalho em Altura;"
						+ "</div> ->PET - Permissão de Entrada em Espaços Confinados;"
						+ " </div>Tudo de forma automática. </div> "
						+ " </div>Pode ter certeza que conosco você terá "
						+ " redução de custo e muita produtividade.<div> "
						+ "</div><div>Nosso foco é criar processos ágeis na Segurança do Trabalho! </div>"
						+ "</div><div>Dentro de Nosso Sistema terá a Parte de Treinamentos que você "
						+ "poderá baixar tanto para cursos quanto para gerar os documentos Gratuítos.<div> "
						+ "</div><div>Temos uma Parte em Nosso sistema "
						+ "que é Pró onde você irá Ter acesso ao nosso Suporte.</div> "
						+ "</div><div>Consulte por favor em nosso Site todos os nossos valores e caso tenha interesse faça "
						+ "o Upgrade para o Plano PRO Ok ! "
						+ "No mais muito obrigado ! </div>"
						+ "</div>"

						+ "<div>Att,</div>" + "<div>Equipe JB Segurança do Trabalho</div>" + "</div>");
		return mailSenderDto;
	}

	private MailSenderDto recuperarMessage(Usuario usuario, String novaSenha) {
		// criando o conteúdo da mensagem
		MailSenderDto mailSenderDto = new MailSenderDto();
		mailSenderDto.setMailTo(usuario.getEmail());
		mailSenderDto.setSubject("Recuperação de senha de usuário realizada com sucesso - JB Segurança do Trabalho");
		mailSenderDto.setBody("<div>" + "<h2>Parabéns " + usuario.getNome() + "!</h2>"
				+ "<div>Uma nova senha foi gerada com sucesso!</div>" + "<div>Acesse o sistema com a senha: <strong>"
				+ novaSenha + "</strong></div>" + "<div>Att,</div>" + "<div>Equipe JB Segurança do Trabalho</div>"
				+ "</div>");
		return mailSenderDto;
	}

}
