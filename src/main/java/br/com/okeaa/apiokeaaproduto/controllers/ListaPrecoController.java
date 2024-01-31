package br.com.okeaa.apiokeaaproduto.controllers;

import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ListaPrecoResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.deposito.ApiDepositoException;
import br.com.okeaa.apiokeaaproduto.service.listaPreco.ListaPrecoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")                   //Padrão para os métodos /api/...
@Api(value = "API REST SELECIONA LISTA DE PREÇO")    //Swagger
@CrossOrigin(origins = "*")                          // Liberar os dominios da API
@Validated
public class ListaPrecoController {

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public ListaPrecoService listaPrecoService;

    @GetMapping("/selecionarListas")
    public List<ListaPrecoResponse> getAllListas() {
        return listaPrecoService.getAllListas();
    }

    @GetMapping("/selecionarLista/{idLista}")
    public ResponseEntity<ListaPrecoResponse> getListayId(@PathVariable String idLista) {
        Optional<ListaPrecoResponse> listaOptional = listaPrecoService.getListaById(idLista);
        if (listaOptional.isPresent()) {
            return ResponseEntity.ok(listaOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletarLista/{idLista}")
    public ResponseEntity<Void> deleteListaById(@PathVariable String idLista) {
        try {
            listaPrecoService.deleteListaById(idLista);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/adicionarLista", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveLista(@RequestBody @Valid String listaPrecoResponse) {
        try {
            String response = listaPrecoService.saveLista(listaPrecoResponse).getBody();
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(path = "/atualizarLista/{idLista}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Atualizar um deposito existente")
    public ResponseEntity<String> updateDeposit(@RequestBody @Valid String listaPrecoResponse, @PathVariable String idLista) {
        try {
            String request = listaPrecoService.updateLista(listaPrecoResponse, idLista).getBody();

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            throw new ApiDepositoException("Houve algum erro sistemico, tente novamente", e);
        }
    }
}
