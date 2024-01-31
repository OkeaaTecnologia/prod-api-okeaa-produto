package br.com.okeaa.apiokeaaproduto.repositories.categoria;

import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.CategoriaResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaResponseRepository extends JpaRepository<CategoriaResponse, Long> {

    Optional<CategoriaResponse> findById(Long id);

    @Query("SELECT c.descricao FROM CategoriaResponse c")
    List<String> findAllDescricao();

    @Query("SELECT c FROM CategoriaResponse c WHERE c.descricao = :descricao")
    List<CategoriaResponse> findByDescricao(@Param("descricao") String descricao);
}
