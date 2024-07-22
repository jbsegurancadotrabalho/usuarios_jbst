package br.com.jbst.exception;

public class PerfilNaoEncontradoException extends Exception {

	private static final long serialVersionUID = 1L;

	public PerfilNaoEncontradoException(String mensagem) {
		super(mensagem);
	}
}
