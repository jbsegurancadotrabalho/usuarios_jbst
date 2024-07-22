package br.com.jbst.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.com.jbst.filters.AuthenticationFilter;
@Configuration
public class AuthenticationConfig {
	//método para configurar quais endpoints da API
	//vão exigir um token de autenticação
	@Bean
	public FilterRegistrationBean<AuthenticationFilter> jwtFilter() {
		//registrando o filtro para validação dos tokens
		FilterRegistrationBean<AuthenticationFilter> filter = new FilterRegistrationBean<AuthenticationFilter>();
		filter.setFilter(new AuthenticationFilter());
		
		//mapear os endpoints da API que serão validados pelo filter
		filter.addUrlPatterns("/api/usuarios/atualizar-dados");		
		return filter;
	}
	
}