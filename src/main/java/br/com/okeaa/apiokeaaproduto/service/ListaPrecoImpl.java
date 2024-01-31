package br.com.okeaa.apiokeaaproduto.service;

import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ListaPrecoResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco.ProdutoLista;
import br.com.okeaa.apiokeaaproduto.repositories.listaPreco.ListaPrecoRequestRepository;
import br.com.okeaa.apiokeaaproduto.repositories.listaPreco.ListaPrecoResponseRepository;
import br.com.okeaa.apiokeaaproduto.repositories.listaPreco.ProdutoListaRepository;
import br.com.okeaa.apiokeaaproduto.service.listaPreco.ListaPrecoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ListaPrecoImpl implements ListaPrecoService {
    @Autowired
    public  ListaPrecoResponseRepository listaPrecoResponseRepository;

    @Autowired
    public ListaPrecoRequestRepository listaPrecoRequestRepository;

    @Autowired
    public ProdutoListaRepository produtoListaRepository;

    @Override
    public List<ListaPrecoResponse> getAllListas() {

        return listaPrecoResponseRepository.findAll();
    }

    @Override
    public Optional<ListaPrecoResponse> getListaById(String idLista) {
        Optional<ListaPrecoResponse> listaOptional = listaPrecoResponseRepository.findByIdLista(idLista);
        if (listaOptional.isPresent()) {
            return listaOptional;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de preço não encontrada com o ID: " + idLista);
        }
    }

    @Override
    @Transactional
    public void deleteListaById(String idLista) {
        Optional<ListaPrecoResponse> listaOptional = listaPrecoResponseRepository.deleteByIdLista(idLista);
        if (listaOptional.isPresent()) {
            listaPrecoResponseRepository.deleteByIdLista(idLista);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de preço não encontrada com o ID: " + idLista);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveLista(String listaPrecoResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ListaPrecoResponse listaPrecoResponseObject = objectMapper.readValue(listaPrecoResponse, ListaPrecoResponse.class);

            listaPrecoResponseObject.setIdLista(generateShortId());

            // Salva a ListaPrecoResponse no banco de dados
            listaPrecoResponseRepository.save(listaPrecoResponseObject);

            // Itera sobre a lista de ProdutoLista associada ao ListaPrecoResponse e salva cada objeto
            for (ProdutoLista produtoLista : listaPrecoResponseObject.getProdutoLista()) {
                produtoLista.setListaPreco(listaPrecoResponseObject);
                produtoListaRepository.save(produtoLista);
            }

            return ResponseEntity.ok("Lista de preço salva com sucesso.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao desserializar o JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar a lista de preço: " + e.getMessage());
        }
    }

    private String generateShortId() {
        // Gera um número aleatório entre 0 e 999999
        int randomNum = (int) (Math.random() * 1000000);
        // Formata o número como uma string com zeros à esquerda, se necessário
        return String.format("%06d", randomNum);
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateLista(String listaPrecoResponse, String idLista) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ListaPrecoResponse listaPrecoResponseObject = objectMapper.readValue(listaPrecoResponse, ListaPrecoResponse.class);

            // Verifica se a entidade com o ID fornecido existe no banco de dados
            Optional<ListaPrecoResponse> existingEntity = listaPrecoResponseRepository.findByIdLista(idLista);
            if (existingEntity.isPresent()) {
                // Exclui os produtos associados à lista existente
                produtoListaRepository.deleteByListaPreco(existingEntity.get());

                // Atualiza os atributos da entidade existente com os valores do JSON
                ListaPrecoResponse listaPrecoResponsePut = existingEntity.get();
                listaPrecoResponsePut.setNomeLista(listaPrecoResponseObject.getNomeLista());
                listaPrecoResponsePut.setBaseado(listaPrecoResponseObject.getBaseado());
                listaPrecoResponsePut.setRegraLista(listaPrecoResponseObject.getRegraLista());
                listaPrecoResponsePut.setFatorAplicado(listaPrecoResponseObject.getFatorAplicado());
                listaPrecoResponsePut.setTipoLista(listaPrecoResponseObject.getTipoLista());

                // Atualiza a associação com os produtos
                List<ProdutoLista> produtos = listaPrecoResponseObject.getProdutoLista();
                for (ProdutoLista produto : produtos) {
                    produto.setListaPreco(listaPrecoResponsePut);
                    produtoListaRepository.save(produto);
                }

                // Salva a ListaPrecoResponse atualizada no banco de dados
                listaPrecoResponseRepository.save(listaPrecoResponsePut);

                return ResponseEntity.ok("Lista de preço atualizada com sucesso.");
            } else {
                return ResponseEntity.ok().body("Lista de preço não encontrada para atualização.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao desserializar o JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar a lista de preço: " + e.getMessage());
        }
    }
}