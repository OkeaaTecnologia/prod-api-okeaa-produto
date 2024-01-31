package br.com.okeaa.apiokeaaproduto.repositories.listaPreco;

import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ListaPrecoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaPrecoResponseRepository extends JpaRepository<ListaPrecoResponse, Long> {

    List<ListaPrecoResponse> findAll();

    Optional<ListaPrecoResponse> findByIdLista(String idLista);

    Optional<ListaPrecoResponse> deleteByIdLista(String idLista);
}

