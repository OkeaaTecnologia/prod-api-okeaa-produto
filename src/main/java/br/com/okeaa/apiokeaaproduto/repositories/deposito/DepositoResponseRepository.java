package br.com.okeaa.apiokeaaproduto.repositories.deposito;

import br.com.okeaa.apiokeaaproduto.controllers.response.deposito.DepositoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepositoResponseRepository extends JpaRepository<DepositoResponse, Long> {

    Optional<DepositoResponse> findById(Long id);

    @Query("SELECT c.descricao FROM DepositoResponse c")
    List<String> findAllDescricao();

    @Query("SELECT c FROM DepositoResponse c WHERE c.descricao = :descricao")
    List<DepositoResponse> findByDescricao(@Param("descricao") String descricao);

}

