package br.com.jbst.producers;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioMessageProducer {
	/*
	 * Componente para acessarmos o servidor do RabbitMQ
	 */
	@Autowired
	private RabbitTemplate rabbitTemplate;
	/*
	 * Componente para acessarmos filas do servidor
	 */
	@Autowired
	private Queue queue;
	
	/*
	 * Método para gravar uma mensagem na fila
	 */
	public void sendMessage(String message) {
		rabbitTemplate.convertAndSend(this.queue.getName(), message);
	}
}
