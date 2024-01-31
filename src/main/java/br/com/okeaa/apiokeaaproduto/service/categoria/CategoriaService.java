package br.com.okeaa.apiokeaaproduto.service.categoria;

import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface CategoriaService {

    JsonResponse getAllCategory();

    JsonResponse getCategoryByIdCategoria(@PathVariable("idCategoria") String idCategoria);

    ResponseEntity<String> createCategory(@RequestBody String xmlCategoria);

    ResponseEntity<String> updateCategory(@RequestBody String xmlCategoria, @PathVariable("idCategoria") String idCategoria) throws ParserConfigurationException, IOException, SAXException;
}