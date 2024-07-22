package br.com.jbst.DTO;

import java.util.UUID;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AtualizarDadosRequestDto {
	@NotEmpty(message = "Por favor, informe o ID do usuário autenticado.")
	private UUID id;
	
	@Pattern(regexp = "^[A-Za-zÀ-Üà-ü\\s .]{8,100}$",
			message = "Informe um nome válido de 8 a 100 caracteres.")
	private String nome;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Por favor, informe uma senha forte com no mímimo 8 caracteres.")
	private String senhaAtual;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Por favor, informe uma senha forte com no mímimo 8 caracteres.")
	private String novaSenha;
}
