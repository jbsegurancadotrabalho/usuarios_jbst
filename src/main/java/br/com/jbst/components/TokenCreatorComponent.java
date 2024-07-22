package br.com.jbst.components;

import java.util.Date;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class TokenCreatorComponent {


	/*
	 * @Value
	 * Capturar o valor de propriedades/chave
	 * mapeadas no application.properties
	 */
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	/*
	 * Método para fazermos a geração dos TOKENS JWT
	 */
	public String generateToken(String id) throws Exception {
		return Jwts.builder()
				.setSubject(id) //id do usuário autenticado
				.setIssuedAt(new Date()) //data de geração do token
				.signWith(SignatureAlgorithm.HS256, jwtSecret) //assinatura antifalsificação
				.compact(); //finalizando e retornando o TOKEN gerado
	}
	
	/*
	 * Método para ler o id do usuário gravado no token
	 */
	public String getIdFromToken(String accessToken) throws Exception {
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(accessToken)
				.getBody()
				.getSubject();
	}
}


