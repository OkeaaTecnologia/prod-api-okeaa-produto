package br.com.okeaa.apiokeaaproduto.service.produto;

import br.com.okeaa.apiokeaaproduto.controllers.response.produto.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

public interface ProdutoService {

//    JsonResponse getAllProducts(int pagina);

    CompletableFuture<JsonResponse> getAllProducts(int pagina);

    JsonResponse getListProducts(String descricao, String codigo, String gtin);

    JsonResponse getProductByCode(@PathVariable("codigo") String codigo);

    JsonResponse getProductByCodeSupplier(@PathVariable("codigoFabricante") String codigoFabricante, @PathVariable("idFabricante") String idFabricante);

    ResponseEntity<String> deleteProductByCode(@PathVariable("codigo") String codigo);

    ResponseEntity<String> createProduct(@RequestBody String xmlProdutos);

    ResponseEntity<String> updateProduct(@RequestBody @Valid String xmlProdutos, @PathVariable("codigo") String codigo);
}
