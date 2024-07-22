package br.com.jbst.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.jbst.DTO.AtualizarDadosRequestDto;
import br.com.jbst.DTO.AtualizarDadosResponseDto;
import br.com.jbst.DTO.AutenticarRequestDto;
import br.com.jbst.DTO.AutenticarResponseDto;
import br.com.jbst.DTO.CriarContaRequestDto;
import br.com.jbst.DTO.CriarContaResponseDto;
import br.com.jbst.DTO.PerfilDto;
import br.com.jbst.DTO.RecuperarSenhaRequestDto;
import br.com.jbst.DTO.RecuperarSenhaResponseDto;
import br.com.jbst.services.UsuarioService;
import jakarta.validation.Valid;



@RestController
@RequestMapping(value = "/api/usuarios")
public class UsuariosController {
	
	@Autowired
	private UsuarioService usuarioService;


	   @PostMapping("autenticar")
	    public ResponseEntity<AutenticarResponseDto> autenticar(@RequestBody @Valid AutenticarRequestDto dto) throws Exception {
	        AutenticarResponseDto response = usuarioService.autenticar(dto);
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    }

	    // Adicionando tratamento para OPTIONS
	    @RequestMapping(value = "autenticar", method = RequestMethod.OPTIONS)
	    public ResponseEntity<?> handleOptions() {
	        return ResponseEntity.ok().build();
	    }

	
	@PostMapping("criar-conta")
	public ResponseEntity<CriarContaResponseDto> criarConta(@RequestBody @Valid CriarContaRequestDto dto) throws Exception {
		return usuarioService.criarConta(dto);
	}
	
	@PostMapping("recuperar-senha")
	public ResponseEntity<RecuperarSenhaResponseDto> recuperarSenha(@RequestBody @Valid RecuperarSenhaRequestDto dto) throws Exception {
		RecuperarSenhaResponseDto response = usuarioService.recuperarSenha(dto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping("atualizar-dados")
	public ResponseEntity<AtualizarDadosResponseDto> atualizarDados(
			@RequestBody @Valid AtualizarDadosRequestDto dto, 
			@RequestHeader("Authorization") String authorizationHeader) throws Exception {
		
		String accessToken = authorizationHeader.replace("Bearer ", "");
		
		AtualizarDadosResponseDto response = usuarioService.atualizarDados(dto, accessToken);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("consultar-perfis")
	public ResponseEntity<List<PerfilDto>> consultarTodosPerfis() throws Exception {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(usuarioService.ConsultarTodosOsPerfis(toString()));	
		
	}
	
	@GetMapping("consultar-usuarios")
	public ResponseEntity<List<CriarContaResponseDto>> consultarTodosUsuarios() throws Exception {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(usuarioService.ConsultarTodosOsUsuarios(toString()));	
		
	}
	
}






