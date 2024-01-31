package br.com.okeaa.apiokeaaproduto.controllers;

import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.JsonResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.categoria.ApiCategoriaException;
import br.com.okeaa.apiokeaaproduto.exceptions.categoria.CategoriaIdCategoriaException;
import br.com.okeaa.apiokeaaproduto.exceptions.categoria.CategoriaListaException;
import br.com.okeaa.apiokeaaproduto.service.categoria.CategoriaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1")        //Padrão para os métodos /api/...
@Api(value = "API REST CATEGORIAS")    //Swagger
@CrossOrigin(origins = "*")        // Liberar os dominios da API
@Validated
public class CategoriaController {

    public static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    public  CategoriaService categoriaService;


    /**
     * GET "BUSCA A LISTA DE CATEGORIAS".
     */
    @GetMapping("/categorias")
    @ApiOperation(value = "Retorna uma lista de categorias")
    public ResponseEntity<JsonResponse> getAllCategory() {
        try {
            JsonResponse request = categoriaService.getAllCategory();

            if (request.retorno.categorias == null && request.retorno.erros == null) {
                throw new CategoriaListaException("Não foi possível localizar a lista de categorias");
            }

            logger.info("GET: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiCategoriaException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * GET "BUSCA CATEGORIA PELO IDCATEGORIA".
     */
    @GetMapping("/categoria/{idCategoria}")
    @ApiOperation(value = "Retorna uma categoria pelo idCategoria")
    public ResponseEntity<JsonResponse> getCategoryByIdCategory(@PathVariable("idCategoria") String idCategoria) {
        try {
            JsonResponse request = categoriaService.getCategoryByIdCategoria(idCategoria);

            if (request.retorno.categorias == null && request.retorno.erros == null) {
                throw new CategoriaIdCategoriaException("Contato com o número de CPF/CNPJ: " + idCategoria + " não encontrado.");
            }

            logger.info("GET ID: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiCategoriaException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * POST "CADASTRA UMA NOVA CATEGORIA UTILIZANDO XML".
     */
    @PostMapping(path = "/cadastrarcategoria", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Cadastrar uma categoria")
    public ResponseEntity<String> createCategory(@RequestBody @Valid String xmlCategoria) {
        try {
            String request = categoriaService.createCategory(xmlCategoria).getBody();

            logger.info("POST: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiCategoriaException("Houve algum erro sistemico, tente novamente", e);
        }
    }

    /**
     * PUT "ATUALIZA UMA CATEGORIA EXISTENTE UTILIZANDO XML".
     */
    @PutMapping(path = "/atualizarcategoria/{idCategoria}", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Atualiza uma categoria")
    public ResponseEntity<String> updateCategory(@RequestBody String xmlCategoria, @PathVariable("idCategoria") String idCategoria) {
        try {
            String request = categoriaService.updateCategory(xmlCategoria, idCategoria).getBody();

            logger.info("UPDATE: " + request);

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiCategoriaException("Houve algum erro sistemico, tente novamente", e);
        }
    }
}