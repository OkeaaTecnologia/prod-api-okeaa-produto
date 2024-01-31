package br.com.okeaa.apiokeaaproduto.controllers;

import br.com.okeaa.apiokeaaproduto.controllers.request.produtoFornecedor.JsonRequest;
import br.com.okeaa.apiokeaaproduto.controllers.response.produtoFornecedor.JsonResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.produtoFornecedor.*;
import br.com.okeaa.apiokeaaproduto.service.produtoFornecedor.ProdutoFornecedorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1")                  //Padrão para os métodos /api/...
@Api(value = "API REST PRODUTOS FORNECEDORES")      //Swagger
@CrossOrigin(origins = "*")                         //Liberar os dominios da API
public class ProdutoFornecedorController {

    public static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    public ProdutoFornecedorService produtoFornecedorService;

    /**
     * GET "BUSCAR LISTA DE PRODUTOS FORNECEDORES".
     */
    @GetMapping("/produtosfornecedores")
    @ApiOperation(value = "Retorna uma lista de produtos fornecedors")
    public ResponseEntity<JsonResponse> getAllProducts() {
        try {
            JsonResponse request = produtoFornecedorService.getAllProducts();

            if (request.retorno.produtosfornecedores == null && request.retorno.erros == null) {
                throw new ProdutoFornecedorListaException("Não foi possível localizar a lista de produto fornecedor");
            }

            logger.info("GET: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request);
        } catch (Exception e) {
            throw new ApiProdutoFornecedorException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * GET "BUSCAR UM PRODUTO FORNECEDOR PELO IDPRODUTOFORNECEDOR".
     */
    @GetMapping("/produtofornecedor/{idProdutoFornecedor}")
    @ApiOperation(value = "Retorna um produto fornecedor pelo idProdutoFornecedor")
    public ResponseEntity<JsonResponse> getProducId(@PathVariable String idProdutoFornecedor) {
        try {
            JsonResponse request = produtoFornecedorService.getProducId(idProdutoFornecedor);

            if (request.retorno.produtosfornecedores == null && request.retorno.erros == null) {
                throw new ProdutoFornecedorIdException("Não foi possível localizar o produto fornecedor pelo Id: " + idProdutoFornecedor);
            }

            logger.info("GET ID: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request);
        } catch (Exception e) {
            throw new ApiProdutoFornecedorException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * POST "CADASTRA UM NOVO PRODUTO FORNECEDOR" UTILIZANDO XML.
     */
    @PostMapping(path = "/cadastrarprodutofornecedor", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cadastra um novo produto fornecedor")
    public ResponseEntity<JsonRequest> createProduct(@RequestBody String xmlProdutoFornecedor) {
        try {
            JsonRequest request = produtoFornecedorService.createProduct(xmlProdutoFornecedor);

            if (request.retorno.produtosfornecedores == null && request.retorno.erros == null) {
                throw new ProdutoFornecedorCadastroException("Cadastro não efetuado, revise os campos e tente novamente!");
            }

            logger.info("POST: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request);

        } catch (Exception e) {
            throw new ApiProdutoFornecedorException("Houve algum erro sistemico, possivelmente o produto fornecedor está associado a algum produto, tente novamente", e);
        }
    }

    /**
     * PUT "ATUALIZA PRODUTO FORNECEDOR EXISTENTE PELO IDPRODUTOFORNECEDOR UTILIZANDO XML".
     */
    @PutMapping(path = "/atualizarprodutofornecedor/{idProdutoFornecedor}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Atualiza um produto fornecedor existente")
    public ResponseEntity<JsonRequest> updateProduct(@RequestBody String xmlProdutoFornecedor, @PathVariable("idProdutoFornecedor") String idProdutoFornecedor) {
        try {
            JsonRequest request = produtoFornecedorService.updateProduct(xmlProdutoFornecedor, idProdutoFornecedor);

            if (request.retorno.produtosfornecedores == null && request.retorno.erros == null) {
                throw new ProdutoFornecedorAtualizarException("Não foi possível atualizar o produto fornecedor pelo Id: " + idProdutoFornecedor);
            }

            logger.info("UPDATE: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request);

        } catch (Exception e) {
            throw new ApiProdutoFornecedorException("Houve algum erro sistemico, tente novamente", e);
        }
    }
}