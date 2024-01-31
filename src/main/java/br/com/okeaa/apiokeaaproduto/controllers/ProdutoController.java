package br.com.okeaa.apiokeaaproduto.controllers;

import br.com.okeaa.apiokeaaproduto.controllers.response.produto.JsonResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.produto.ApiProdutoException;
import br.com.okeaa.apiokeaaproduto.exceptions.produto.ProdutoCodigoException;
import br.com.okeaa.apiokeaaproduto.exceptions.produto.ProdutoCodigoFornecedorException;
import br.com.okeaa.apiokeaaproduto.exceptions.produto.ProdutoExclusaoException;
import br.com.okeaa.apiokeaaproduto.service.produto.ProdutoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping(value = "/api/v1")        //Padrão para os métodos /api/...
@Api(value = "API REST PRODUTOS")    //Swagger
@CrossOrigin(origins = "*")        // Liberar os dominios da API
public class ProdutoController {

    public static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    public ProdutoService produtoService;

    public String codigo;

    /**
     * GET "BUSCAR LISTA DE PRODUTOS".
     */
//    @GetMapping("/produtos/page={pagina}")
//    @ApiOperation(value = "Retorna uma lista de produtos")
//    public ResponseEntity<JsonResponse> getAllProducts(@PathVariable int pagina) {
//        try {
//            JsonResponse request = produtoService.getAllProducts(pagina);
//
//            logger.info("GET: " + request);
//
//            return ResponseEntity.status(HttpStatus.OK).body(request);
//
//        } catch (Exception e) {
//            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
//        }
//    }

    @GetMapping("/produtos/page={pagina}")
    @ApiOperation(value = "Retorna uma lista de contatos")
    public CompletableFuture<JsonResponse> getAllProducts(@PathVariable int pagina) {
        try {
            CompletableFuture<JsonResponse> request = produtoService.getAllProducts(pagina);

            logger.info("GET: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request).getBody();
        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistêmico, tente novamente", e);
        }
    }

    @GetMapping("/produtos")
    @ApiOperation(value = "Retorna uma lista de contatos")
    public ResponseEntity<JsonResponse> getListContacts(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String gtin
    ) {
        try {
            JsonResponse request = produtoService.getListProducts(descricao, codigo, gtin);

            logger.info("GET: " + request);

            return ResponseEntity.status(HttpStatus.OK).body(request);
        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistêmico, tente novamente", e);
        }
    }

    /**
     * GET "BUSCAR UM PRODUTO PELO CÒDIGO (SKU)".
     */
    @GetMapping("/produto/{codigo}")
    @ApiOperation(value = "Retorna um produto pelo código")
    public ResponseEntity<JsonResponse> getProductByCode(@PathVariable String codigo) {
        try {
            JsonResponse request = produtoService.getProductByCode(codigo);

            if (request.retorno.produtos == null && request.retorno.erros == null) {
                throw new ProdutoCodigoException("Produto com código " + codigo + " não localizado.");
            }

            logger.info("GET ID: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * GET "PRODUTO UTILIZANDO O CODIGOFABRICANTE E IDFABRICANTE".
     */
    @GetMapping("/produto/{codigoFabricante}/{idFabricante}")
    @ApiOperation(value = "Retorna um produto pelo código e idFabricante")
    public ResponseEntity<JsonResponse> getProductByCodeSupplier(@PathVariable String codigoFabricante, @PathVariable String idFabricante) {
        JsonResponse request = produtoService.getProductByCodeSupplier(codigoFabricante, idFabricante);
        try {

            if (request.retorno.produtos == null && request.retorno.erros == null) {
                throw new ProdutoCodigoFornecedorException("Produto com código: " + codigoFabricante + " e idFabricante: " + idFabricante + " não localizado.");
            }

            logger.info("GET ID+FAB: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * DELETE PROUTO PELO CÓDIGO (SKU).
     */
    @DeleteMapping("/produto/{codigo}")
    @ApiOperation(value = "Deletar um produto pelo código")
    public ResponseEntity<Object> deleteProductByCode(@PathVariable String codigo) {
        try {
            JsonResponse request = produtoService.getProductByCode(codigo);

            if (request.retorno.produtos == null && request.retorno.erros == null) {
                throw new ProdutoExclusaoException("Cadastro não deletado, revise o código do produto e tente novamente!");
            }
            produtoService.deleteProductByCode(codigo);

            logger.info("Codigo deletado = " + codigo);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * POST "CADASTRAR UM NOVO PRODUTO UTILIZANDO XML".
     */
    @PostMapping(path = "/cadastrarproduto", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cadastrar um novo produto")
    public ResponseEntity<String> createProduct(@RequestBody @Valid String xmlProdutos) {
        try {
            String request = produtoService.createProduct(xmlProdutos).getBody();

            logger.info("POST: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * POST "ATUALIZAR UM PRODUTO EXISTENTE PELO CODIGO UTILIZANDO XML".
     */
    @PostMapping(path = "/atualizarproduto/{codigo}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Atualizar um produto existente")
    public ResponseEntity<String> updateProduct(@RequestBody @Valid String xml, @PathVariable String codigo) {
        try {
            String request = produtoService.updateProduct(xml, codigo).getBody();

            logger.info("UPDATE: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiProdutoException("Houve algum erro sistemico, tente novamente", e);
        }
    }
}