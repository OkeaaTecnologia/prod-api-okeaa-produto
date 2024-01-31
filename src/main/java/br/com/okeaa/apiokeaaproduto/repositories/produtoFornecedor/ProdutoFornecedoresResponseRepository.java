package br.com.okeaa.apiokeaaproduto.repositories.produtoFornecedor;

import br.com.okeaa.apiokeaaproduto.controllers.response.produtoFornecedor.ProdutoFornecedoresResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoFornecedoresResponseRepository extends JpaRepository<ProdutoFornecedoresResponse, Long> {
    Optional<ProdutoFornecedoresResponse> findById(Long id);
}

