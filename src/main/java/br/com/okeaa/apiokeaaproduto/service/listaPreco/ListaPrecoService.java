package br.com.okeaa.apiokeaaproduto.service.listaPreco;

import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ListaPrecoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface ListaPrecoService {

    List<ListaPrecoResponse> getAllListas();

    Optional<ListaPrecoResponse> getListaById(String idLista);

    void deleteListaById(String idLista);

    ResponseEntity<String> saveLista(@RequestBody String listaPrecoResponse);

    ResponseEntity<String> updateLista(@RequestBody String listaPrecoResponse, @PathVariable("idLista") String idLista);


}
