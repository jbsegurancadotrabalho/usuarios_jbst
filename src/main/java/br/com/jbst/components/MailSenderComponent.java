package br.com.jbst.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import br.com.jbst.DTO.MailSenderDto;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailSenderComponent {
	/*
	 * Injetando dependência para o componente do Spring Mail que fará o disparo das
	 * mensagens
	 */
	@Autowired
	private JavaMailSender javaMailSender;

	/*
	 * Ler um valor do /application.properties referente a conta de email utilizada
	 * para enviar as mensagens
	 */
	@Value("${spring.mail.username}")
	private String userName;

	/*
	 * Método para fazer o envio dos emails
	 */
	public void sendMessage(MailSenderDto dto) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(userName);
		helper.setTo(dto.getMailTo());
		helper.setSubject(dto.getSubject());
		helper.setText(dto.getBody(), true);
		javaMailSender.send(message);
	}
}
