package br.com.okeaa.apiokeaaproduto.service.deposito;

import br.com.okeaa.apiokeaaproduto.controllers.response.deposito.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface DepositoService {

    JsonResponse getAllDeposit();

    JsonResponse getDepositById(@PathVariable("idDeposito") String idDeposito);

    ResponseEntity<String> createDeposit(@RequestBody String xmlDeposito);

    ResponseEntity<String> updateDeposit(@RequestBody String xmlDeposito, @PathVariable("idDeposito") String idDeposito);

}
