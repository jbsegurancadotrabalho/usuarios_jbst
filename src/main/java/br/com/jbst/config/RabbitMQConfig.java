package br.com.jbst.config;


import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	/*
	 * Capturando o nome da fila configurado
	 * no arquivo /application.properties
	 */
	@Value("${queue.name}")
	private String queueName;
	
	/*
	 * Configurando a fila que o RabbitMQ
	 * irá acessar no servidor
	 */
	@Bean
	public Queue queue() {
		return new Queue(queueName, true);
	}
}
