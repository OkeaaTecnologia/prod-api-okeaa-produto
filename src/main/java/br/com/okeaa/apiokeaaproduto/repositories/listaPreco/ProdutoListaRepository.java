package br.com.okeaa.apiokeaaproduto.repositories.listaPreco;

import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ListaPrecoResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ProdutoLista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProdutoListaRepository extends JpaRepository<ProdutoLista, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ProdutoLista p WHERE p.listaPreco = ?1")
    void deleteByListaPreco(ListaPrecoResponse listaPreco);
}
