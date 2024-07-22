package br.com.jbst.data;


import java.time.Instant;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import br.com.jbst.components.MD5Component;
import br.com.jbst.entities.Perfil;
import br.com.jbst.entities.Usuario;
import br.com.jbst.repositories.PerfilRepository;
import br.com.jbst.repositories.UsuarioRepository;

@Component
public class LoadData implements ApplicationRunner {


	@Autowired
	private PerfilRepository perfilRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;


	@Autowired
	private MD5Component md5Component;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		// verificando se a tabela de perfil já possui um Perfil ADMIN...
		Perfil perfilMaster = perfilRepository.findByNome("Master");
		if (perfilMaster== null) {


			perfilMaster = new Perfil();
			perfilMaster.setId(UUID.randomUUID());
			perfilMaster.setNome("Master");


			perfilRepository.save(perfilMaster);
		}


		// verificando se a tabela de perfil já possui um Perfil ADMIN...
		Perfil perfilEmpresa = perfilRepository.findByNome("Empresa");
		if (perfilEmpresa == null) {


			perfilEmpresa = new Perfil();
			perfilEmpresa.setId(UUID.randomUUID());
			perfilEmpresa.setNome("Empresa");


			perfilRepository.save(perfilEmpresa);
		}
		
				// verificando se a tabela de perfil já possui um Perfil ADMIN...
		Perfil perfilAlunoParticular = perfilRepository.findByNome("Aluno Particular");
		if (perfilAlunoParticular == null) {


			perfilAlunoParticular = new Perfil();
			perfilAlunoParticular.setId(UUID.randomUUID());
			perfilAlunoParticular.setNome("Aluno Particular");


			perfilRepository.save(perfilAlunoParticular);
		}
		

		
		Usuario usuarioMaster = usuarioRepository.findByEmail("operacional@jbsegurancadotrabalho.com.br");
	if(usuarioMaster == null) {
		
		usuarioMaster = new Usuario();
		usuarioMaster.setId(UUID.randomUUID());
		usuarioMaster.setNome("Usuário Master");
		usuarioMaster.setEmail("operacional@jbsegurancadotrabalho.com.br");
		usuarioMaster.setSenha(md5Component.encrypt("@Admin1234"));
		usuarioMaster.setDataHoraCriacao(Instant.now());
		usuarioMaster.setDataHoraAlteracao(Instant.now());
		usuarioMaster.setPerfil(perfilMaster);
		
		usuarioRepository.save(usuarioMaster);
	}
	
	}
	
}
