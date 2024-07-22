package br.com.jbst.handle;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class Problema {
	
	private Integer status;
	private LocalDateTime dataHora;
	private String titulo;
	private List<Campo>campos;
	private List<String>Errors;
	
	@AllArgsConstructor
	@Setter
	@Getter
	public static class Campo{
		
		private String nome;
		private String mensagem;
		
	}
}