package br.com.okeaa.apiokeaaproduto.repositories.produto;

import br.com.okeaa.apiokeaaproduto.controllers.response.produto.ProdutoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoResponseRepository extends JpaRepository<ProdutoResponse, Long> {

    Optional<ProdutoResponse> findById(Long id);

    @Query("SELECT c.codigo FROM ProdutoResponse c")
    List<String> findAllCodigo();

    @Query("SELECT c FROM ProdutoResponse c WHERE c.codigo = :codigo")
    Optional<ProdutoResponse> findByCodigo(@Param("codigo") String codigo);

    @Query("SELECT c FROM ProdutoResponse c WHERE c.codigoFabricante = :codigoFabricante AND c.idFabricante = :idFabricante")
    Optional<ProdutoResponse> findByFabricante(@Param("codigoFabricante") String codigoFabricante, @Param("idFabricante") String idFabricante);

    @Query("SELECT c FROM ProdutoResponse c WHERE c.descricao = :descricao")
    List<ProdutoResponse> findByDescricao(@Param("descricao") String descricao);

    @Query("SELECT c FROM ProdutoResponse c WHERE LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :descricao, '%')) OR LOWER(c.nomeFornecedor) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<ProdutoResponse> findContactByDescricao(@Param("descricao") String descricao);

    @Query("SELECT c FROM ProdutoResponse c WHERE LOWER(c.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')) OR LOWER(c.gtin) LIKE LOWER(CONCAT('%', :codigo, '%')) ")
    List<ProdutoResponse> findContactByCodeOrGtin(@Param("codigo") String codigo);

}
