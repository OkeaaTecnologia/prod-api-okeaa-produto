package br.com.okeaa.apiokeaaproduto.service;

import br.com.okeaa.apiokeaaproduto.controllers.ProdutoController;
import br.com.okeaa.apiokeaaproduto.controllers.request.categoria.CategoriaRequest;
import br.com.okeaa.apiokeaaproduto.controllers.request.categoria.JsonRequest;
import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.CategoriaResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.JsonResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.categoria.RetornoResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.categoria.ApiCategoriaException;
import br.com.okeaa.apiokeaaproduto.repositories.categoria.CategoriaRequestRepository;
import br.com.okeaa.apiokeaaproduto.repositories.categoria.CategoriaResponseRepository;
import br.com.okeaa.apiokeaaproduto.service.categoria.CategoriaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoriaServiceImpl implements CategoriaService {

    public static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Value("${external.api.url}")
    public String apiBaseUrl;

    @Value("${external.api.apikey}")
    public String apiKey;

    @Value("${external.api.apikeyparam}")
    public String apikeyparam;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public CategoriaService categoriaService;

    @Autowired
    public CategoriaResponseRepository categoriaResponseRepository;

    @Autowired
    public CategoriaRequestRepository categoriaRequestRepository;

    /**
     * GET "BUSCAR A LISTA DE CATEGORIA CADASTRADOS NO BLING".
     * Método responsável por buscar a lista de produtos, tanto na API externa quanto no banco de dados local.
     *
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public JsonResponse getAllCategory() throws ApiCategoriaException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(response.getBody(), JsonResponse.class);

            // Cria uma lista de Categorias com os valores da API Bling.
//            List<CategoriaResponse> categorias = new ArrayList<>();
//            for (RetornoResponse.Categorias categoria : jsonResponse.getRetorno().getCategorias()) {
//                categorias.add(categoria.getCategoria());
//            }

            List<CategoriaResponse> categorias = new ArrayList<>();
            if (jsonResponse.getRetorno() != null && jsonResponse.getRetorno().getCategorias() != null) {
                for (RetornoResponse.Categorias categoria : jsonResponse.getRetorno().getCategorias()) {
                    categorias.add(categoria.getCategoria());
                }
            }

            // Cria uma lista de Categorias de resposta para enviar de volta
            ArrayList<RetornoResponse.Categorias> categoriasResponse = new ArrayList<>();
            // Percorre todas as categorias da lista
            for (CategoriaResponse categoria : categorias) {
                // Verifica se a categoria existe no banco de dados
                Optional<CategoriaResponse> categoriaExistente = categoriaResponseRepository.findById(categoria.getId());
                if (categoriaExistente.isPresent()) {
                    // Se a categoria já existir, atualiza seus campos.
                    CategoriaResponse categoriaAtualizada = categoriaExistente.get();
                    categoriaAtualizada.setId(categoria.getId());
                    categoriaAtualizada.setDescricao(categoria.getDescricao());
                    categoriaAtualizada.setIdCategoriaPai(categoria.getIdCategoriaPai());
                    categoriaResponseRepository.save(categoriaAtualizada);
                } else {
                    // Sea categoria não existir, insere uma nova cattegoria no banco de dados
                    categoriaResponseRepository.save(categoria);
                }
                // Adiciona a categoria de resposta à lista de categorias de resposta
                RetornoResponse.Categorias categoriaResponse = new RetornoResponse.Categorias();
                categoriaResponse.setCategoria(categoria);
                categoriasResponse.add(categoriaResponse);
            }
            // Cria o objeto de resposta final
            RetornoResponse retornoResponse = new RetornoResponse();
            retornoResponse.setCategorias(categoriasResponse);

            JsonResponse jsonRetornoResponse = new JsonResponse();
            jsonRetornoResponse.setRetorno(retornoResponse);

            //Retorna a resposta final em formato JSON
            return jsonRetornoResponse;

//            return jsonResponse;
        } catch (JsonProcessingException e) {
            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
        } catch (RestClientException e) {
            // Busca todas as categorias salvas no banco de dados
            List<CategoriaResponse> categorias = categoriaResponseRepository.findAll();
            // Verifica se a lista de categorias está vazia, ou seja, se não há nenhuma categoria cadastrada no banco de dados
            if (categorias.isEmpty()) {
                // Se a lista de categoria estiver vazia, lança uma exceção ApiCategoriaException com uma mensagem de erro e a exceção original
                throw new ApiCategoriaException("Erro ao chamar API: ", e);
            } else {
                // Se houver categorias cadastradas no banco de dados, cria uma nova lista de categorias para o retorno da API
                RetornoResponse retornoResponse = new RetornoResponse();
                ArrayList<RetornoResponse.Categorias> categoriasResponse = new ArrayList<>();
                // Para cada categoria salva no banco de dados, cria um novo objeto RetornoResponse.Categorias com a categoria correspondente e adiciona na lista de categorias do retorno
                for (CategoriaResponse categoria : categorias) {
                    RetornoResponse.Categorias categoriaResponse = new RetornoResponse.Categorias();
                    categoriaResponse.setCategoria(categoria);
                    categoriasResponse.add(categoriaResponse);
                }
                // Define a lista de categorias do retorno e cria um objeto JsonResponse com esse retorno
                retornoResponse.setCategorias(categoriasResponse);
                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(retornoResponse);
                // Retorna o objeto JsonResponse
                return jsonResponse;
            }
        }
    }

    /**
     * GET "BUSCA CATEGORIA PELO IDCATEGORIA".
     * Método responsável por localizar uma categoria a partir do seu idCategoria, tanto na API externa quanto no banco de dados local.
     *
     * @param idCategoria idCategoria a ser localizado.
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public JsonResponse getCategoryByIdCategoria(String idCategoria) throws ApiCategoriaException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/categoria/" + idCategoria + "/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(response.getBody(), JsonResponse.class);

            return jsonResponse;

        } catch (JsonProcessingException e) {
            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
        } catch (RestClientException e) {
            // Busca a categoria com o código informado no banco de dados
            Optional<CategoriaResponse> categoriaExistente = categoriaResponseRepository.findById(Long.valueOf(idCategoria));
            // Se a categoria existir no banco de dados, cria um objeto CategoriaResponse com a categoria encontrada e retorna como resposta
            if (categoriaExistente.isPresent()) {
                RetornoResponse.Categorias categoria = new RetornoResponse.Categorias();
                categoria.setCategoria(categoriaExistente.get());

                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(new RetornoResponse());

                jsonResponse.getRetorno().setCategorias(new ArrayList<>());
                jsonResponse.getRetorno().getCategorias().add(categoria);

                return jsonResponse;

            } else {
                throw new ApiCategoriaException("A API está indisponível e a categoria não foi encontrada no banco de dados.", e);
            }
        }
    }

    /**
     * POST "CADASTRA UMA NOVA CATEGORIA UTILIZANDO XML".
     * Método responsável por cadastrar uma categoria, tanto na API externa quanto no banco de dados local.
     *
     * @param xmlCategoria xml com os dados do cadastro da nova categoria.
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public ResponseEntity<String> createCategory(String xmlCategoria) throws ApiCategoriaException {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("apikey", apiKey);
            map.add("xml", xmlCategoria);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            String url = apiBaseUrl + "/categoria/json/";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonRequest jsonRequest = objectMapper.readValue(responseEntity.getBody(), JsonRequest.class);

            return responseEntity;

        } catch (RestClientException e) {
            // Em caso de erro ao chamar a API, salva os dados no banco de dados
            CategoriaRequest categoriaRequest = new CategoriaRequest();
            // Preenche os dados de categoriaRequest com os valores passados em xmlCategoria
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(xmlCategoria));
                Document doc = builder.parse(is);

                String nomeCategoria = doc.getElementsByTagName("descricao").item(0).getTextContent();
                List<CategoriaRequest> categoriaExistente = categoriaRequestRepository.findByDescricao(nomeCategoria);
                boolean categoriaJaExiste = !categoriaExistente.isEmpty();

                if (!categoriaJaExiste) {
                    // Preenchimento dos campos da categoria
                    Node nodeCategoria = doc.getElementsByTagName("categorias").item(0);
                    Element elementoCategoria = (Element) nodeCategoria;

                    categoriaRequest.setDescricao(elementoCategoria.getElementsByTagName("descricao").item(0).getTextContent());
                    String idCategoriaPai = elementoCategoria.getElementsByTagName("idCategoriaPai").item(0).getTextContent();
                    categoriaRequest.setIdCategoriaPai(Integer.valueOf(idCategoriaPai));
                    categoriaRequest.setFlag("POST");

                    categoriaRequestRepository.save(categoriaRequest);
                }

                return ResponseEntity.status(HttpStatus.CREATED).body(""); // Retorna um ResponseEntity vazio com status 200 (OK)

            } catch (ParserConfigurationException | SAXException | IOException ex) {
                throw new ApiCategoriaException("Erro ao processar XML: ", ex);
            }
        } catch (JsonProcessingException e) {
            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
        }
    }

    /**
     * PUT "ATUALIZA UMA CATEGORIA EXISTENTE UTILIZANDO XML".
     * Método responsável por atualizar uma categoria, tanto na API externa quanto no banco de dados local.
     *
     * @param xmlCategoria xml com os dados do cadastro da nova categoria.
     * @param idCategoria  Id para acesso direto a categoria cadastrada.
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public ResponseEntity<String> updateCategory(String xmlCategoria, String idCategoria) throws ApiCategoriaException {
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("apikey", apiKey);
            map.add("xml", xmlCategoria);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            String url = apiBaseUrl + "/categoria/" + idCategoria + "/json/";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonRequest result = objectMapper.readValue(responseEntity.getBody(), JsonRequest.class);

            return responseEntity;

        } catch (JsonProcessingException e) {
            throw new ApiCategoriaException("Erro ao processar JSON", e);
        } catch (RestClientException e) {
            // Caso haja algum erro de conexão ou a API esteja indisponível, tenta atualizar os dados no banclogger.info("API Bling Categoria [PUT] indisponível, atualizando a categoria no banco de dados");

            Optional<CategoriaResponse> optionalCategoria = categoriaResponseRepository.findById(Long.valueOf(idCategoria));
            if (optionalCategoria.isPresent()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(xmlCategoria));
                    Document doc = builder.parse(is);

                    //Adiciona na tabela tb_categoria_request a categoria atualizada e adiciona uma flaf PUT para posterior ser atualizado.
                    CategoriaRequest categoriaRequest = new CategoriaRequest();
                    categoriaRequest.setId(Long.valueOf(idCategoria));
                    categoriaRequest.setDescricao(getTextContent(doc, "descricao"));
                    categoriaRequest.setIdCategoriaPai(getIntegerContent(doc, "idcategoriapai"));
                    categoriaRequest.setFlag("PUT");

                    // Verifique se a categoria existe na tabela de categoriaRequest
                    String descricaoCategoriaRequest = doc.getElementsByTagName("descricao").item(0).getTextContent();
                    List<CategoriaRequest> caategoriaRequestExistente = categoriaRequestRepository.findByDescricao(descricaoCategoriaRequest);
                    boolean categoriaRequestExiste = !caategoriaRequestExistente.isEmpty();
                    //Se a categoria não existe na tabela, o mesmo é cadastrado.
                    if (!categoriaRequestExiste) {
                        logger.info("Categoria não encontrada no Banco de dados, adicionando... [PUT]");
                        categoriaRequestRepository.save(categoriaRequest);
                    }

                    //Atualiza na tabela tb_categoria_response a categoria atualizada para acesso imediato.
                    CategoriaResponse categoriaResponse = new CategoriaResponse();
                    categoriaResponse.setId(Long.valueOf(idCategoria));
                    categoriaResponse.setDescricao(categoriaRequest.getDescricao());
                    categoriaResponse.setIdCategoriaPai(String.valueOf(categoriaRequest.getIdCategoriaPai()));

                    // Verifique se o produto existe na tabela de ProdutoResponse
                    String descricaoCategoriaResponse = doc.getElementsByTagName("descricao").item(0).getTextContent();
                    List<CategoriaResponse> categoriaResponseExistente = categoriaResponseRepository.findByDescricao(descricaoCategoriaResponse);
                    boolean categoriaResponseExiste = !categoriaResponseExistente.isEmpty();
                    //Se o produto existe na tabela, o mesmo é atualizado
                    if (categoriaResponseExiste) {
                        logger.info("Dados atualizados no banco de dados.");
                        categoriaResponseRepository.save(categoriaResponse);
                    }

                    return ResponseEntity.status(HttpStatus.CREATED).body(""); // Retorna um ResponseEntity vazio com status 200 (OK)

                } catch (NumberFormatException | ParserConfigurationException | IOException | SAXException ex) {
                    throw new ApiCategoriaException("Erro ao processar XML", ex);
                }
            } else {
                throw new ApiCategoriaException("Categoria não encontrada no banco de dados", e);
            }
        }
    }

    // Função auxiliar para obter o conteúdo de um nó de texto
    public String getTextContent(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes != null && nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    // Função auxiliar para obter o conteúdo de um nó de texto como Integer
    public Integer getIntegerContent(Document doc, String tagName) {
        String content = getTextContent(doc, tagName);
        return content != null ? Integer.valueOf(content) : null;
    }

    /**
     * Verifica o status da API externa e atualiza o banco de dados local com as categorias cadastradas na API.
     * Este método é executado periodicamente, com o intervalo de tempo definido na propriedade "api.check.delay".
     * Se uma categoria existe apenas no banco de dados local, ela será adicionada na API.
     * Se uma categoria existe tanto no banco de dados local quanto na API, ela será deletada do banco de dados local.
     *
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponível para a consulta.
     */
    @Scheduled(fixedDelayString = "${api.check.delay}")
    public void scheduledPostCategory() {
        try {
            logger.info("---------- Scheduled POST Categoria ----------");
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<CategoriaRequest> categorias = categoriaRequestRepository.findAll();
                List<String> descricaoCategorias = categoriaResponseRepository.findAllDescricao();

                if (categorias.isEmpty()) {
                    logger.info("Não há categoria no banco de dados para atualizar no Bling");
                    logger.info("-------------------------------------------");
                } else {
                    for (CategoriaRequest categoria : categorias) {
                        if (categoria.getFlag() != null && categoria.getFlag().equals("POST")) {
                            if (!descricaoCategorias.contains(categoria.getDescricao())) {

                                String xmlCategoria = "<categorias>";
                                xmlCategoria += "<categoria>";
                                xmlCategoria += "<descricao>" + categoria.getDescricao() + "</descricao>";
                                xmlCategoria += "<idCategoriaPai>" + categoria.getIdCategoriaPai() + "</idCategoriaPai>";
                                xmlCategoria += "</categoria>";
                                xmlCategoria += "</categorias>";

                                ResponseEntity<String> createResponse = createCategory(xmlCategoria);

                                if (createResponse.getStatusCode() == HttpStatus.CREATED) {
                                    categoriaRequestRepository.delete(categoria);
                                }
                                logger.info("-------------------------------------------");
                            } else {
                                logger.info("Categoria já existe na API, deletando...");
                                categoriaRequestRepository.delete(categoria);
                            }
                        }
                    }
                }
            } else {
                logger.info(" ERRO na função scheduledPostCategory [POST]");
            }
        } catch (RestClientException e) {
            logger.info("API Bling Categoria está offline, nada a fazer");
        }
    }

    /**
     * Verifica o status da API externa e atualiza o banco de dados local com as categorias cadastradas na API.
     * Este método é executado periodicamente, com o intervalo de tempo definido na propriedade "api.check.delay".
     * Se uma categoria existe apenas no banco de dados local, ela será adicionada na API.
     * Se uma categoria existe tanto no banco de dados local quanto na API, ela será deletada do banco de dados local.
     *
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponível para a consulta.
     */
    @Scheduled(fixedDelayString = "${api.check.delay}")
    public void scheduledUpdateCategory() {
        try {
            logger.info("--------- Scheduled PUT Categoria ---------");
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<CategoriaRequest> categoriaRequests = categoriaRequestRepository.findAll();

                if (categoriaRequests.isEmpty()) {
                    logger.info("Não há categoria no banco de dados para atualizar no Bling");
                    logger.info("-------------------------------------------");
                } else {
                    for (CategoriaRequest categoriaRequest : categoriaRequests) {
                        if ("PUT".equals(categoriaRequest.getFlag())) { // verifica se a flag é "PUT"

                            String xmlCategoria = "<categorias>";
                            xmlCategoria += "<categoria>";
                            xmlCategoria += "<descricao>" + categoriaRequest.getDescricao() + "</descricao>";
                            xmlCategoria += "<idCategoriaPai>" + categoriaRequest.getIdCategoriaPai() + "</idCategoriaPai>";
                            xmlCategoria += "</categoria>";
                            xmlCategoria += "</categorias>";

                            String idCategoria = String.valueOf(categoriaRequest.getId());

                            ResponseEntity<String> createResponse = updateCategory(xmlCategoria, idCategoria);

                            if (createResponse.getStatusCode() == HttpStatus.OK) {
                                categoriaRequestRepository.delete(categoriaRequest);
                            }
                            logger.info("-------------------------------------------");
                        }
                    }
                }
            } else {
                logger.info("ERRO na função scheduledUpdateCategory [PUT]");
            }
        } catch (RestClientException e) {
            logger.info("API Bling Categoria está offline, nada a fazer");
        }
    }

    /*
      --------------------------------------------- VERSÃO 1 - SEM CONEXÃO E ACESSO AO BANCO DE DADOS. ----------------------------------------------------------
     */

    /*
     * GET "BUSCAR A LISTA DE CATEGORIA CADASTRADOS NO BLING".
     */
//    @Override
//    public JsonResponse getAllCategory() throws ApiCategoriaException {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(headers);
//
//            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonResponse result = objectMapper.readValue(response.getBody(), JsonResponse.class);
//
//            return result;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
//        } catch (RestClientException e) {
//            throw new ApiCategoriaException("Erro ao chamar API: ", e);
//        }
//    }

    /*
     * GET "BUSCA CATEGORIA PELO IDCATEGORIA".
     */
//    @Override
//    public JsonResponse getCategoryByIdCategoria(String idCategoria) throws ApiCategoriaException {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(idCategoria, headers);
//
//            String url = apiBaseUrl + "/categoria/" + idCategoria + "/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonResponse result = objectMapper.readValue(response.getBody(), JsonResponse.class);
//
//            return result;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
//        } catch (RestClientException e) {
//            throw new ApiCategoriaException("Erro ao chamar API: ", e);
//        }
//    }

    /*
     * POST "CADASTRA UMA NOVA CATEGORIA UTILIZANDO XML".
     */

//    @Override
//    public JsonRequest createCategory(String xmlCategoria) throws ApiCategoriaException {
//        try {
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("apikey", apiKey);
//            map.add("xml", xmlCategoria);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
//            String url = apiBaseUrl + "/categoria/json/";
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonRequest result = objectMapper.readValue(response.getBody(), JsonRequest.class);
//
//            return result;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiCategoriaException("Erro ao processar JSON: ", e);
//        } catch (RestClientException e) {
//            throw new ApiCategoriaException("Erro ao chamar API", e);
//        }
//    }

    /*
     * PUT "CADASTRA UMA NOVA CATEGORIA UTILIZANDO XML".
     */
//    @Override
//    public JsonRequest updateCategory(String xmlCategoria, String idCategoria) throws ApiCategoriaException {
//        try {
//            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//            map.add("apikey", apiKey);
//            map.add("xml", xmlCategoria);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
//            String url = apiBaseUrl + "/categoria/" + idCategoria + "/json/";
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonRequest result = objectMapper.readValue(response.getBody(), JsonRequest.class);
//
//            return result;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiCategoriaException("Erro ao processar JSON", e);
//        } catch (RestClientException e) {
//            throw new ApiCategoriaException("Erro ao chamar API", e);
//        }
//    }
}

