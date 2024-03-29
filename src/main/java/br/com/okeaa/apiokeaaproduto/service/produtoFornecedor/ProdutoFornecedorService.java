package br.com.okeaa.apiokeaaproduto.service.produtoFornecedor;

import br.com.okeaa.apiokeaaproduto.controllers.request.produtoFornecedor.JsonRequest;
import br.com.okeaa.apiokeaaproduto.controllers.response.produtoFornecedor.JsonResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProdutoFornecedorService {

    public JsonResponse getAllProducts();

    public JsonResponse getProducId(@PathVariable("idProdutoFornecedor") String idProdutoFornecedor);

    public JsonRequest createProduct(@RequestBody String xmlProdutoFornecedor);

    public JsonRequest updateProduct(@RequestBody String xmlProdutoFornecedor, @PathVariable("idProdutoFornecedor") String idProdutoFornecedor);
}
