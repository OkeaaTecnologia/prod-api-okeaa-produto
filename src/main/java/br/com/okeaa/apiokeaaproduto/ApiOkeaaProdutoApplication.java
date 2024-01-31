package br.com.okeaa.apiokeaaproduto;

import br.com.okeaa.apiokeaaproduto.service.categoria.CategoriaService;
import br.com.okeaa.apiokeaaproduto.service.deposito.DepositoService;
import br.com.okeaa.apiokeaaproduto.service.produto.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ApiOkeaaProdutoApplication {

	@Autowired
	public CategoriaService categoriaService;

	@Autowired
	public DepositoService depositoService;

	@Autowired
	public ProdutoService produtoService;

	public static void main(String[] args) {
		SpringApplication.run(ApiOkeaaProdutoApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Este método é chamado uma vez no início.
		doInit();
	}

//	@Scheduled(fixedRate = 300000) // 30.000 milissegundos = 05 Minutos.
//	public void scheduledInit() {
//		// Este método será chamado periodicamente a cada 05 minutos.
//		doInit();
//	}

	public void doInit() {
		// Defina os valores desejados para paginação
		int pagina = 1; // substitua pelo número da página desejada

		produtoService.getAllProducts(pagina);
		categoriaService.getAllCategory();
		depositoService.getAllDeposit();
	}

}
