package br.com.jbst.handle;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.jbst.DTO.ErrorResponseDto;
import br.com.jbst.exception.UsuarioException;

@ControllerAdvice
public class UsuarioExceptionHandler {
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	 @ExceptionHandler(IllegalArgumentException.class)
	public ErrorResponseDto handleUserException(UsuarioException e) {
		
		List<String> errors = new ArrayList<String>();
		errors.add(e.getMessage());
		
		ErrorResponseDto response = new ErrorResponseDto();
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setErrors(errors);
		
		return response;
	}

}
