package br.com.okeaa.apiokeaaproduto.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
		"br.com.okeaa.apiokeaaproduto.repositories.categoria",
		"br.com.okeaa.apiokeaaproduto.repositories.deposito",
		"br.com.okeaa.apiokeaaproduto.repositories.produto",
		"br.com.okeaa.apiokeaaproduto.repositories.produtoFornecedor",
		"br.com.okeaa.apiokeaaproduto.repositories.listaPreco"
})
@EntityScan(basePackages = {
		"br.com.okeaa.apiokeaaproduto.controllers.response.categoria",
		"br.com.okeaa.apiokeaaproduto.controllers.response.deposito",
		"br.com.okeaa.apiokeaaproduto.controllers.response.produto",
		"br.com.okeaa.apiokeaaproduto.controllers.response.produtoFornecedor",
		"br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco",

		"br.com.okeaa.apiokeaaproduto.controllers.request.categoria",
		"br.com.okeaa.apiokeaaproduto.controllers.request.deposito",
		"br.com.okeaa.apiokeaaproduto.controllers.request.produto",
		"br.com.okeaa.apiokeaaproduto.controllers.request.produtoFornecedor",
		"br.com.okeaa.apiokeaaproduto.controllers.request.listaPreco"
})
@ComponentScan(basePackages = {
		"br.com.okeaa.apiokeaaproduto.controllers.request.categoria",
		"br.com.okeaa.apiokeaaproduto.controllers.request.deposito",
		"br.com.okeaa.apiokeaaproduto.controllers.request.produto",
		"br.com.okeaa.apiokeaaproduto.controllers.request.produtoFornecedor",
		"br.com.okeaa.apiokeaaproduto.controllers.request.listaPreco"
})
public class ApplicationConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}