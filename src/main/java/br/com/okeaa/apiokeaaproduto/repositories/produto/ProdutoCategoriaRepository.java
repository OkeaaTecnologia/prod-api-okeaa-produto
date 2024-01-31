package br.com.okeaa.apiokeaaproduto.repositories.produto;

import br.com.okeaa.apiokeaaproduto.controllers.response.produto.ProdutoCategoriaResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  ProdutoCategoriaRepository extends JpaRepository<ProdutoCategoriaResponse, Long> {

    Optional<ProdutoCategoriaResponse> findById(Long id);


}
