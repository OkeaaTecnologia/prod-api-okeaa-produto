package br.com.okeaa.apiokeaaproduto.repositories.produto;

import br.com.okeaa.apiokeaaproduto.controllers.request.produto.ProdutoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRequestRepository extends JpaRepository<ProdutoRequest, Long> {

    Optional<ProdutoRequest> findById(Long idProduto);

    @Query("SELECT c FROM ProdutoRequest c WHERE c.descricao = :descricao")
    List<ProdutoRequest> findByDescricao(@Param("descricao") String descricao);

    @Query("SELECT c FROM ProdutoRequest c WHERE c.codigo = :codigo")
    Optional<ProdutoRequest> findByCodigo(@Param("codigo") String codigo);

}
