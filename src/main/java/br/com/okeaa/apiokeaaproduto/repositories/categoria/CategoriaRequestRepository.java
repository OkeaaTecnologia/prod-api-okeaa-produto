package br.com.okeaa.apiokeaaproduto.repositories.categoria;

import br.com.okeaa.apiokeaaproduto.controllers.request.categoria.CategoriaRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRequestRepository extends JpaRepository<CategoriaRequest, Long> {

    Optional<CategoriaRequest> findById(Long id);
    @Query("SELECT c FROM CategoriaRequest c WHERE c.descricao = :descricao")
    List<CategoriaRequest> findByDescricao(@Param("descricao") String descricao);
}


