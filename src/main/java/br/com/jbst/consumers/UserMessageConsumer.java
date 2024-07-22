//package br.com.jbst.consumers;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import br.com.jbst.DTO.MailSenderDto;
//import br.com.jbst.components.MailSenderComponent;
//
//@Service
//public class UserMessageConsumer {
//
//	@Autowired
//	MailSenderComponent mailSenderComponent;
//	
//	@Autowired
//	ObjectMapper objectMapper;
//	
//	/*
//	 * Método para ler os itens da fila
//	 */
//	@RabbitListener(queues = { "${queue.name}" })
//	public void receive(@Payload String message) {
//
//		try {
//			
//			//deserializar a mensagem gravada na fila
//			MailSenderDto dto = objectMapper.readValue(message, MailSenderDto.class);	
//			
//			//enviar por email
//			mailSenderComponent.sendMessage(dto.getMailTo(), dto.getSubject(), dto.getBody());			
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}		
//	}	
//}