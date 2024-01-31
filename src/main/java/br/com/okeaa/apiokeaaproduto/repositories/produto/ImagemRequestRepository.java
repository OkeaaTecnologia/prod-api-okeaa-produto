package br.com.okeaa.apiokeaaproduto.repositories.produto;

import br.com.okeaa.apiokeaaproduto.controllers.request.produto.ImagemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemRequestRepository extends JpaRepository<ImagemRequest, Long> {

}
