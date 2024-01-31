package br.com.okeaa.apiokeaaproduto.service;

import br.com.okeaa.apiokeaaproduto.controllers.ProdutoController;
import br.com.okeaa.apiokeaaproduto.controllers.request.produto.ImagemRequest;
import br.com.okeaa.apiokeaaproduto.controllers.request.produto.ProdutoRequest;
import br.com.okeaa.apiokeaaproduto.controllers.response.produto.JsonResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.produto.ProdutoResponse;
import br.com.okeaa.apiokeaaproduto.controllers.response.produto.RetornoResponse;
import br.com.okeaa.apiokeaaproduto.exceptions.categoria.ApiCategoriaException;
import br.com.okeaa.apiokeaaproduto.exceptions.produto.ApiProdutoException;
import br.com.okeaa.apiokeaaproduto.repositories.produto.ImagemRequestRepository;
import br.com.okeaa.apiokeaaproduto.repositories.produto.ProdutoCategoriaRepository;
import br.com.okeaa.apiokeaaproduto.repositories.produto.ProdutoRequestRepository;
import br.com.okeaa.apiokeaaproduto.repositories.produto.ProdutoResponseRepository;
import br.com.okeaa.apiokeaaproduto.service.produto.ProdutoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;


@Service
public class ProdutoServiceImpl implements ProdutoService {

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
    public ProdutoResponseRepository produtoResponseRepository;

    @Autowired
    public ProdutoRequestRepository produtoRequestRepository;

    @Autowired
    public ProdutoCategoriaRepository produtoCategoriaRepository;

    @Autowired
    public ImagemRequestRepository imagemRequestRepository;


    /**
     * GET "BUSCAR A LISTA DE PRODUTOS CADASTRADO NO BLING".
     * Método responsável por buscar a lista de produtos, tanto na API externa quanto no banco de dados local.
     *
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
//    @Override
//    public JsonResponse getAllProducts(int pagina) throws ApiProdutoException {
//        try {
//            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
////            String url = "http://www.teste.com/";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(headers);
//
//            String url = apiBaseUrl + "/produtos/page=" + pagina + "/json/" + apikeyparam + apiKey;
//
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonResponse jsonResponse = objectMapper.readValue(response.getBody(), JsonResponse.class);
//
//            List<ProdutoResponse> produtos = new ArrayList<>();
//
//            List<RetornoResponse.Produtos> produtosListPage = jsonResponse.getRetorno().getProdutos();
//
//            // Cria uma lista de Produtos com os valores da API Bling
//            if (produtosListPage != null ) {
//                for (RetornoResponse.Produtos produto : produtosListPage) {
//                    produtos.add(produto.getProduto());
//                }
//            }
//
//            // Cria uma lista de Produtos de resposta para enviar de volta
//            ArrayList<RetornoResponse.Produtos> produtosResponse = new ArrayList<>();
//            // Percorre todos os produtos da lista
//            for (ProdutoResponse produto : produtos) {
//                // Verifica se o produto existe no banco de dados
//                Optional<ProdutoResponse> produtoExistente = produtoResponseRepository.findById(produto.getId());
//                if (produtoExistente.isPresent()) {
//                    // Se o produto já existir, atualiza seus campos.
//                    ProdutoResponse produtoAtualizado = produtoExistente.get();
//                    //Tela Caracteristicas
//                    produtoAtualizado.setId(produto.getId());
//                    produtoAtualizado.setCodigo(produto.getCodigo());
//                    produtoAtualizado.setDescricao(produto.getDescricao());
//                    produtoAtualizado.setTipo(produto.getTipo());
//                    produtoAtualizado.setSituacao(produto.getSituacao());
//                    produtoAtualizado.setUnidade(produto.getUnidade());
//                    produtoAtualizado.setPreco(produto.getPreco());
//                    produtoAtualizado.setDescricaoCurta(produto.getDescricaoCurta());
//                    produtoAtualizado.setDescricaoComplementar(produto.getDescricaoComplementar());
//                    produtoAtualizado.setDataInclusao(produto.getDataInclusao());
//                    produtoAtualizado.setDataAlteracao(produto.getDataAlteracao());
//                    produtoAtualizado.setUrlVideo(produto.getUrlVideo());
//                    produtoAtualizado.setMarca(produto.getMarca());
//                    produtoAtualizado.setLinkExterno(produto.getLinkExterno());
//                    produtoAtualizado.setObservacoes(produto.getObservacoes());
//                    produtoAtualizado.setPesoLiq(produto.getPesoLiq());
//                    produtoAtualizado.setPesoBruto(produto.getPesoBruto());
//                    produtoAtualizado.setGtin(produto.getGtin());
//                    produtoAtualizado.setGtinEmbalagem(produto.getGtinEmbalagem());
//                    produtoAtualizado.setLarguraProduto(produto.getLarguraProduto());
//                    produtoAtualizado.setAlturaProduto(produto.getAlturaProduto());
//                    produtoAtualizado.setProfundidadeProduto(produto.getProfundidadeProduto());
//                    produtoAtualizado.setUnidadeMedida(produto.getUnidadeMedida());
//                    produtoAtualizado.setItensPorCaixa(produto.getItensPorCaixa());
//                    produtoAtualizado.setVolumes(produto.getVolumes());
//                    produtoAtualizado.setCondicao(produto.getCondicao());
//                    produtoAtualizado.setFreteGratis(produto.getFreteGratis());
//                    produtoAtualizado.setProducao(produto.getProducao());
//                    produtoAtualizado.setDataValidade(produto.getDataValidade());
//                    //Tela Imagem.
//                    produtoAtualizado.setImageThumbnail(produto.getImageThumbnail());
//                    //Tela Estoque.
//                    produtoAtualizado.setCrossdocking(produto.getCrossdocking());
//                    produtoAtualizado.setEstoqueMinimo(produto.getEstoqueMinimo());
//                    produtoAtualizado.setEstoqueMaximo(produto.getEstoqueMaximo());
//                    produtoAtualizado.setLocalizacao(produto.getLocalizacao());
//                    //Tela Fornecedores
//                    produtoAtualizado.setNomeFornecedor(produto.getNomeFornecedor());
//                    produtoAtualizado.setCodigoFabricante(produto.getCodigoFabricante());
//                    produtoAtualizado.setGarantia(produto.getGarantia());
//                    produtoAtualizado.setDescricaoFornecedor(produto.getDescricaoFornecedor());
//                    produtoAtualizado.setPrecoCusto(produto.getPrecoCusto());
//                    produtoAtualizado.setIdFabricante(produto.getIdFabricante());
//                    //Tela Tributação.
//                    produtoAtualizado.setClass_fiscal(produto.getClass_fiscal());
//                    produtoAtualizado.setCest(produto.getCest());
//                    produtoAtualizado.setOrigem(produto.getOrigem());
//                    produtoAtualizado.setIdGrupoProduto(produto.getIdGrupoProduto());
//                    produtoAtualizado.setGrupoProduto(produto.getGrupoProduto());
//                    produtoAtualizado.setSpedTipoItem(produto.getSpedTipoItem());
//                    //Lista de desconto.
//                    produtoAtualizado.setPrecoDescontoLista(produto.getPrecoDescontoLista());
//                    produtoAtualizado.setListaDesconto(produto.getListaDesconto());
//
//                    //Lista de categoria que cada produto possui
////                    ProdutoCategoriaResponse categoriaAtualizada = new ProdutoCategoriaResponse();
////                    categoriaAtualizada.setId(produto.getCategoria().getId());
////                    categoriaAtualizada.setDescricao(produto.getCategoria().getDescricao());
////                    produtoAtualizado.setCategoria(categoriaAtualizada);
//
//                    produtoResponseRepository.save(produtoAtualizado);
//
//                } else {
//                    ProdutoCategoriaResponse categoria = produto.getCategoria();
//                    if (categoria != null) {
//                        Optional<ProdutoCategoriaResponse> categoriaExistente = produtoCategoriaRepository.findById(Long.valueOf(categoria.getId()));
//                        if (categoriaExistente.isPresent()) {
//                            // Atualize a categoria se ela já existe no banco
//                            ProdutoCategoriaResponse categoriaAtualizada = categoriaExistente.get();
//                            categoriaAtualizada.setId(categoria.getId());
//                            categoriaAtualizada.setDescricao(categoria.getDescricao());
//                            produto.setCategoria(categoriaAtualizada); // Atualiza a categoria do produto
//                            produtoCategoriaRepository.save(categoriaAtualizada);
//                        } else {
//                            // Salve a nova categoria no banco
//                            produtoCategoriaRepository.save(categoria);
//                        }
//                    }
//                    // Agora que a categoria foi salva ou atualizada e associada ao produto, salve o produto no banco
//                    produtoResponseRepository.save(produto);
//                }
//
//                // Adiciona o produto de resposta à lista de produtos de resposta
//                RetornoResponse.Produtos produtoResponse = new RetornoResponse.Produtos();
//                produtoResponse.setProduto(produto);
//                produtosResponse.add(produtoResponse);
//            }
//            // Cria o objeto de resposta final
//            RetornoResponse retornoResponse = new RetornoResponse();
//            retornoResponse.setProdutos(produtosResponse);
//
//            JsonResponse jsonRetornoResponse = new JsonResponse();
//            jsonRetornoResponse.setRetorno(retornoResponse);
//
//            // Retorna a resposta final em formato JSON
//            return jsonRetornoResponse;
//
////            return jsonResponse;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiProdutoException("Erro ao processar JSON: ", e);
//        } catch (RestClientException e) {
//            // Busca todos os produtos salvos no banco de dados
//            List<ProdutoResponse> produtos = produtoResponseRepository.findAll();
//
//            // Verifica se a lista de produtos está vazia, ou seja, se não há nenhum produto cadastrado no banco de dados
//            if (!produtos.isEmpty()) {
//                // Cria uma lista de produtos de resposta para enviar de volta
//                ArrayList<RetornoResponse.Produtos> produtosResponse = new ArrayList<>();
//
//                // Para cada produto salvo no banco de dados, carrega a categoria associada e cria um novo objeto RetornoResponse.Produtos com o produto e a categoria correspondentes
//                for (ProdutoResponse produto : produtos) {
//                    RetornoResponse.Produtos produtoResponse = new RetornoResponse.Produtos();
//                    produtoResponse.setProduto(produto);
//                    produtosResponse.add(produtoResponse);
//                }
//
//                // Define a lista de produtos do retorno e cria um objeto JsonResponse com esse retorno
//                RetornoResponse retornoResponse = new RetornoResponse();
//                retornoResponse.setProdutos(produtosResponse);
//                JsonResponse jsonResponse = new JsonResponse();
//                jsonResponse.setRetorno(retornoResponse);
//
//                // Retorna o objeto JsonResponse
//                return jsonResponse;
//            } else {
//                logger.info("API Bling Produto [GET] indisponível, não há dados para inserir no banco de dados local.");
//                return new JsonResponse(); // Isso cria uma resposta vazia
//            }
//        }
//    }

    @Async
    @Override
    public CompletableFuture<JsonResponse> getAllProducts(int pagina) throws ApiProdutoException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            String urlPage = apiBaseUrl + "/produtos/page=" + pagina + "/json/" + apikeyparam + apiKey;

            ResponseEntity<String> responsePage = restTemplate.exchange(urlPage, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapperPage = new ObjectMapper();
            JsonResponse jsonResponseContatoPage = objectMapperPage.readValue(responsePage.getBody(), JsonResponse.class);

            return CompletableFuture.completedFuture(jsonResponseContatoPage);

        } catch (JsonProcessingException e) {
            throw new ApiProdutoException("Erro ao processar JSON", e);
        } catch (RestClientException e) {

            List<ProdutoResponse> produtos = produtoResponseRepository.findAll();
            if (!produtos.isEmpty()) {
                ArrayList<RetornoResponse.Produtos> produtosResponse = new ArrayList<>();
                for (ProdutoResponse produto : produtos) {
                    RetornoResponse.Produtos produtoResponse = new RetornoResponse.Produtos();
                    produtoResponse.setProduto(produto);
                    produtosResponse.add(produtoResponse);
                }

                RetornoResponse retornoResponse = new RetornoResponse();
                retornoResponse.setProdutos(produtosResponse);
                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(retornoResponse);

                return CompletableFuture.completedFuture(jsonResponse);
            } else {
                logger.info("API Bling Contato [GET] indisponível, não há dados para inserir no banco de dados local.");
                return CompletableFuture.completedFuture(new JsonResponse()); // Isso cria uma resposta vazia
            }
        }
    }

    @Override
    @Transactional
    public JsonResponse getListProducts(String descricao, String codigo, String gtin) throws ApiProdutoException {
        try {
            List<ProdutoResponse> produtos;

            if (descricao != null) {
                produtos = produtoResponseRepository.findContactByDescricao(descricao);
            } else if (codigo != null) {
                produtos = produtoResponseRepository.findContactByCodeOrGtin(codigo);
            } else {
                produtos = produtoResponseRepository.findContactByCodeOrGtin(gtin);
            }

            if (!produtos.isEmpty()) {
                ArrayList<RetornoResponse.Produtos> contatosResponse = new ArrayList<>();

                for (ProdutoResponse produto : produtos) {
                    RetornoResponse.Produtos produtoResponse = new RetornoResponse.Produtos();
                    produtoResponse.setProduto(produto);
                    contatosResponse.add(produtoResponse);
                }

                RetornoResponse retornoResponse = new RetornoResponse();
                retornoResponse.setProdutos(contatosResponse);

                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(retornoResponse);

                return jsonResponse;
            } else {
                logger.info("API Bling Contato [GET] indisponível, não há dados para inserir no banco de dados local.");
                return new JsonResponse(); // Isso cria uma resposta vazia
            }
        } catch (RestClientException e) {
            List<ProdutoResponse> produtos;

            if (descricao != null) {
                produtos = produtoResponseRepository.findContactByDescricao(descricao);
            } else if (codigo != null) {
                produtos = produtoResponseRepository.findContactByCodeOrGtin(codigo);
            } else {
                produtos = produtoResponseRepository.findContactByCodeOrGtin(gtin);
            }

            if (!produtos.isEmpty()) {
                ArrayList<RetornoResponse.Produtos> contatosResponse = new ArrayList<>();

                for (ProdutoResponse produto : produtos) {
                    RetornoResponse.Produtos produtoResponse = new RetornoResponse.Produtos();
                    produtoResponse.setProduto(produto);
                    contatosResponse.add(produtoResponse);
                }

                RetornoResponse retornoResponse = new RetornoResponse();
                retornoResponse.setProdutos(contatosResponse);

                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(retornoResponse);

                return jsonResponse;
            } else {
                logger.info("API Bling Contato [GET] indisponível, não há dados para inserir no banco de dados local.");
                return new JsonResponse(); // Isso cria uma resposta vazia
            }
        }
    }

    /**
     * GET "BUSCAR UM PRODUTO PELO CODIGO (SKU)".
     * Método responsável por localizar um produto a partir do seu código, tanto na API externa quanto no banco de dados local.
     *
     * @param codigo Código do produto a ser localizado.
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public JsonResponse getProductByCode(String codigo) throws ApiProdutoException {
        try {
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            String url = apiBaseUrl + "/produto/" + codigo + "/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse jsonResponse = objectMapper.readValue(response.getBody(), JsonResponse.class);

            return jsonResponse;

        } catch (JsonProcessingException e) {
            throw new ApiProdutoException("Erro ao processar JSON: ", e);
        } catch (RestClientException e) {
            // Busca o produto com o código informado no banco de dados
            Optional<ProdutoResponse> produtoExistente = produtoResponseRepository.findByCodigo(String.valueOf(codigo));
            // Se o produto existir no banco de dados, cria um objeto RetornoResponse.Produtos com o produto encontrado e retorna como resposta
            if (produtoExistente.isPresent()) {
                RetornoResponse.Produtos produto = new RetornoResponse.Produtos();
                produto.setProduto(produtoExistente.get());
                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(new RetornoResponse());
                jsonResponse.getRetorno().setProdutos(new ArrayList<>());
                jsonResponse.getRetorno().getProdutos().add(produto);

                return jsonResponse;

            } else {
                throw new ApiProdutoException("A API está indisponível e os produtos não foram encontrados no banco de dados.", e);
            }
        }
    }

    /**
     * GET "BUSCAR UM PRODUTO PELO CODIGO (SKU) E IDFORNECEDOR"".
     * Método responsável por localizar um produto a partir do seu cosigoFabricante e idFabricante, tanto na API externa quanto no banco de dados local.
     *
     * @param codigoFabricante Código do fabricante e idFabricante a ser localizados.
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public JsonResponse getProductByCodeSupplier(String codigoFabricante, String idFabricante) throws ApiProdutoException {
        try {
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            String url = apiBaseUrl + "/produto/" + codigoFabricante + "/" + idFabricante + "/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonResponse result = objectMapper.readValue(response.getBody(), JsonResponse.class);

            return result;

        } catch (JsonProcessingException e) {
            throw new ApiProdutoException("Erro ao processar JSON", e);
        } catch (RestClientException e) {
            // Busca o produto com o código informado no banco de dados
            Optional<ProdutoResponse> produtoExistente = produtoResponseRepository.findByFabricante(codigoFabricante, idFabricante);
            // Se o produto existir no banco de dados, cria um objeto RetornoResponse.Produtos com o produto encontrado e retorna como resposta
            if (produtoExistente.isPresent()) {
                RetornoResponse.Produtos produto = new RetornoResponse.Produtos();
                produto.setProduto(produtoExistente.get());
                JsonResponse jsonResponse = new JsonResponse();
                jsonResponse.setRetorno(new RetornoResponse());
                jsonResponse.getRetorno().setProdutos(new ArrayList<>());
                jsonResponse.getRetorno().getProdutos().add(produto);

                return jsonResponse;

            } else {
                throw new ApiProdutoException("A API está indisponível e os produtos não foram encontrados no banco de dados.", e);
            }
        }
    }

    /**
     * DELETE "APAGA UM PRODUTO PELO CÓDIGO (SKU)".
     * Método responsável por deletar um produto a partir do seu código, tanto na API externa quanto no banco de dados local.
     *
     * @param codigo Código do produto a ser deletado
     * @return
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa ou na exclusão do produto no banco de dados local.
     */
    @Override
    public ResponseEntity<String> deleteProductByCode(String codigo) throws ApiProdutoException {
        try {
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";

            // Configuração dos cabeçalhos para a requisição HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            // Monta a URL com o código do produto a ser excluído e os parâmetros de autenticação da API externa
            String url = apiBaseUrl + "/produto/" + codigo + "/json/" + apikeyparam + apiKey;

            // Faz a requisição HTTP DELETE para a API externa, passando a URL, o cabeçalho e o tipo de retorno esperado
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

            // Verifica se a requisição na API externa foi bem sucedida
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Se sim, busca o produto no banco de dados local a partir do seu código
                Optional<ProdutoResponse> produtoExistente = produtoResponseRepository.findByCodigo(String.valueOf(codigo));
                if (produtoExistente.isPresent()) {
                    // Se o produto existe no banco de dados local, exclui-o
                    produtoResponseRepository.delete(produtoExistente.get());
                }
            }

            return responseEntity;

        } catch (RestClientException e) {
            Optional<ProdutoRequest> produtoExistenteRequest = produtoRequestRepository.findByCodigo(codigo);
            boolean produtoExiste = produtoExistenteRequest.isEmpty();

            if (produtoExiste) {
                logger.info("Produto não encontrado no Banco de dados, cadastrando... [DELETE]");

                ProdutoRequest produtoRequest = new ProdutoRequest();
                produtoRequest.setCodigo(String.valueOf(codigo));
                produtoRequest.setFlag("DELETE");

                produtoRequestRepository.save(produtoRequest);
            }

            // Caso ocorra algum erro na comunicação com a API externa, lança uma exceção informando o erro
            Optional<ProdutoResponse> produtoExistenteResponse = produtoResponseRepository.findByCodigo(String.valueOf(codigo));
            if (produtoExistenteResponse.isPresent()) {
                // Se o produto existe no banco de dados local, exclui-o
                produtoResponseRepository.delete(produtoExistenteResponse.get());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(""); // Retorna um ResponseEntity vazio com status 200 (OK)
    }

    /**
     * POST "CADASTRA UM NOVO PRODUTO UTILIZANDO XML".
     * Método responsável por cadastrar um produto, tanto na API externa quanto no banco de dados local.
     *
     * @param xmlProdutos xml com os dados do cadastro do novo produto.
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Override
    public ResponseEntity<String> createProduct(String xmlProdutos) throws ApiProdutoException {
        try {
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("apikey", apiKey);
            map.add("xml", xmlProdutos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            String url = apiBaseUrl + "/produto/json/";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            return responseEntity; // Retornar o ResponseEntity completo

        } catch (RestClientException e) {
            // Em caso de erro ao chamar a API, salva os dados no banco de dados
            logger.info("API Bling Produto [POST] indisponível");
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(xmlProdutos));
                Document doc = builder.parse(is);

                // Verifica se o produto existe no banco de dados
                String descricaoProdutoExistente = doc.getElementsByTagName("descricao").item(0).getTextContent();
                List<ProdutoRequest> produtoExistente = produtoRequestRepository.findByDescricao(descricaoProdutoExistente);
                boolean produtoExiste = !produtoExistente.isEmpty();

                if (!produtoExiste) {
                    logger.info("Produto não encontrado no Banco de dados, cadastrando... [POST]");

                    NodeList idNodes = doc.getElementsByTagName("id");
                    Long id = null;
                    if (idNodes != null && idNodes.getLength() > 0) {
                        String idString = idNodes.item(0).getTextContent().trim();
                        if (!"null".equalsIgnoreCase(idString)) {
                            id = Long.valueOf(idString);
                        }
                    }

                    // Mapeamento para ProdutoRequest
                    ProdutoRequest produtoRequest = new ProdutoRequest();

                    Random random = new Random();
                    long UUID = (long) (random.nextDouble() * 1000000); // Gere um número aleatório de 0 a 999999 como ID de 6 dígitos

                    produtoRequest.setId(UUID);
                    produtoRequest.setCodigo(getTextContent(doc, "codigo"));
                    produtoRequest.setCodigoItem(getIntegerContent(doc, "codigoItem"));
                    produtoRequest.setDescricao(getTextContent(doc, "descricao"));
                    produtoRequest.setTipo(getTextContent(doc, "tipo"));
                    produtoRequest.setSituacao(getTextContent(doc, "situacao"));
                    produtoRequest.setDescricaoCurta(getTextContent(doc, "descricaoCurta"));
                    produtoRequest.setDescricaoComplementar(getTextContent(doc, "descricaoComplementar"));
                    produtoRequest.setDescricaoFornecedor(getTextContent(doc, "descricaoFornecedor"));
                    produtoRequest.setUn(getTextContent(doc, "un"));
                    produtoRequest.setVlr_unit(getBigDecimalContent(doc, "vlr_unit"));
                    produtoRequest.setVlr_desc(getBigDecimalContent(doc, "vlr_desc"));
                    produtoRequest.setPreco_custo(getBigDecimalContent(doc, "preco_custo"));
                    produtoRequest.setPeso_bruto(getBigDecimalContent(doc, "peso_bruto"));
                    produtoRequest.setPeso_liq(getBigDecimalContent(doc, "peso_liq"));
                    produtoRequest.setClass_fiscal(getTextContent(doc, "class_fiscal"));
                    produtoRequest.setMarca(getTextContent(doc, "marca"));
                    produtoRequest.setCest(getTextContent(doc, "cest"));
                    produtoRequest.setOrigem(getTextContent(doc, "origem"));
                    produtoRequest.setIdGrupoProduto(getBigDecimalContent(doc, "idGrupoProduto"));
                    produtoRequest.setCondicao(getTextContent(doc, "condicao"));
                    produtoRequest.setFreteGratis(getTextContent(doc, "freteGratis"));
                    produtoRequest.setLinkExterno(getTextContent(doc, "linkExterno"));
                    produtoRequest.setObservacoes(getTextContent(doc, "observacoes"));
                    produtoRequest.setProducao(getTextContent(doc, "producao"));
                    produtoRequest.setUnidadeMedida(getTextContent(doc, "unidadeMedida"));
                    produtoRequest.setDataValidade(getTextContent(doc, "dataValidade"));
                    produtoRequest.setDescricaoFornecedor(getTextContent(doc, "descricaoFornecedor"));
                    produtoRequest.setIdFabricante(getBigDecimalContent(doc, "idFabricante"));
                    produtoRequest.setCodigoFabricante(getTextContent(doc, "codigoFabricante"));
                    produtoRequest.setGtin(getTextContent(doc, "gtin"));
                    produtoRequest.setGtinEmbalagem(getTextContent(doc, "gtinEmbalagem"));
                    produtoRequest.setLargura(getTextContent(doc, "largura"));
                    produtoRequest.setAltura(getTextContent(doc, "altura"));
                    produtoRequest.setProfundidade(getTextContent(doc, "profundidade"));
                    produtoRequest.setEstoqueMinimo(getBigDecimalContent(doc, "estoqueMinimo"));
                    produtoRequest.setEstoqueMaximo(getBigDecimalContent(doc, "estoqueMaximo"));
                    produtoRequest.setEstoque(getIntegerContent(doc, "estoque"));
                    produtoRequest.setItensPorCaixa(getBigDecimalContent(doc, "itensPorCaixa"));
                    produtoRequest.setVolumes(getBigDecimalContent(doc, "volumes"));
                    produtoRequest.setUrlVideo(getTextContent(doc, "urlVideo"));

                    ImagemRequest imagemRequest = new ImagemRequest();
                    String urlDaImagem = getTextContent(doc, "url");
                    imagemRequest.setUrl(urlDaImagem);
                    imagemRequestRepository.save(imagemRequest);

                    produtoRequest.setImagens(imagemRequest);
                    produtoRequest.setLocalizacao(getTextContent(doc, "localizacao"));
                    produtoRequest.setCrossdocking(getBigDecimalContent(doc, "crossdocking"));
                    produtoRequest.setGarantia(getIntegerContent(doc, "garantia"));
                    produtoRequest.setSpedTipoItem(getIntegerContent(doc, "spedTipoItem"));
                    produtoRequest.setIdCategoria(getTextContent(doc, "idCategoria"));
                    produtoRequest.setFlag("POST");

                    produtoRequestRepository.save(produtoRequest);
                }

                return ResponseEntity.status(HttpStatus.OK).body(""); // Retorna um ResponseEntity vazio com status 200 (OK)

            } catch (ParserConfigurationException | SAXException | IOException ex) {
                throw new ApiCategoriaException("Erro ao processar XML: ", ex);
            }
        }
    }

    /**
     * POST / PUT "ATUALIZA UM PRODUTO EXISTENTE UTILIZANDO XML".
     * Método responsável por atualizar um produto, tanto na API externa quanto no banco de dados local.
     *
     * @param xmlProdutos xml com os dados do cadastro do novo produto.
     * @param codigo      codigo para acesso direto ao produto cadastrado.
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponivel para a consulta.
     */
    @Transactional
    @Override
    public ResponseEntity<String> updateProduct(String xmlProdutos, String codigo) throws ApiProdutoException {
        try {
            /* TESTE BANCO DE DADOS, DESCOMENTAR LINHA ABAIXO */
//            String url = "http://www.teste.com/";

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("apikey", apiKey);
            map.add("xml", xmlProdutos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            String url = apiBaseUrl + "/produto/" + codigo + "/json/";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            return responseEntity; // Retornar o ResponseEntity completo

        } catch (RestClientException e) {
            // Caso haja algum erro de conexão ou a API esteja indisponível, tenta atualizar os dados no banco local
            logger.info("API Bling Produto [PUT] indisponível, atualizando o produto no banco de dados");

            Optional<ProdutoResponse> optionalProduto = produtoResponseRepository.findByCodigo(codigo);
            if (optionalProduto.isPresent()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(xmlProdutos));
                    Document doc = builder.parse(is);

                    NodeList idNodes = doc.getElementsByTagName("id");
                    Long id = null;
                    if (idNodes != null && idNodes.getLength() > 0) {
                        String idString = idNodes.item(0).getTextContent().trim();
                        if (!"null".equalsIgnoreCase(idString)) {
                            id = Long.valueOf(idString);
                        }
                    }

                    // Mapeamento para ProdutoRequest
                    ProdutoRequest produtoRequest = new ProdutoRequest();
                    produtoRequest.setId(id);
                    produtoRequest.setCodigo(getTextContent(doc, "codigo"));
                    produtoRequest.setCodigoItem(getIntegerContent(doc, "codigoItem"));
                    produtoRequest.setDescricao(getTextContent(doc, "descricao"));
                    produtoRequest.setTipo(getTextContent(doc, "tipo"));
                    produtoRequest.setSituacao(getTextContent(doc, "situacao"));
                    produtoRequest.setDescricaoCurta(getTextContent(doc, "descricaoCurta"));
                    produtoRequest.setDescricaoComplementar(getTextContent(doc, "descricaoComplementar"));
                    produtoRequest.setUn(getTextContent(doc, "un"));
                    produtoRequest.setVlr_unit(getBigDecimalContent(doc, "vlr_unit"));
                    produtoRequest.setVlr_desc(getBigDecimalContent(doc, "vlr_desc"));
                    produtoRequest.setPreco_custo(getBigDecimalContent(doc, "preco_custo"));
                    produtoRequest.setPeso_bruto(getBigDecimalContent(doc, "peso_bruto"));
                    produtoRequest.setPeso_liq(getBigDecimalContent(doc, "peso_liq"));
                    produtoRequest.setClass_fiscal(getTextContent(doc, "class_fiscal"));
                    produtoRequest.setMarca(getTextContent(doc, "marca"));
                    produtoRequest.setCest(getTextContent(doc, "cest"));
                    produtoRequest.setOrigem(getTextContent(doc, "origem"));
                    produtoRequest.setIdGrupoProduto(getBigDecimalContent(doc, "idGrupoProduto"));
                    produtoRequest.setCondicao(getTextContent(doc, "condicao"));
                    produtoRequest.setFreteGratis(getTextContent(doc, "freteGratis"));
                    produtoRequest.setLinkExterno(getTextContent(doc, "linkExterno"));
                    produtoRequest.setObservacoes(getTextContent(doc, "observacoes"));
                    produtoRequest.setProducao(getTextContent(doc, "producao"));
                    produtoRequest.setUnidadeMedida(getTextContent(doc, "unidadeMedida"));
                    produtoRequest.setDataValidade(getTextContent(doc, "dataValidade"));
                    produtoRequest.setDescricaoFornecedor(getTextContent(doc, "descricaoFornecedor"));
                    produtoRequest.setIdFabricante(getBigDecimalContent(doc, "idFabricante"));
                    produtoRequest.setCodigoFabricante(getTextContent(doc, "codigoFabricante"));
                    produtoRequest.setGtin(getTextContent(doc, "gtin"));
                    produtoRequest.setGtinEmbalagem(getTextContent(doc, "gtinEmbalagem"));
                    produtoRequest.setLargura(getTextContent(doc, "largura"));
                    produtoRequest.setAltura(getTextContent(doc, "altura"));
                    produtoRequest.setProfundidade(getTextContent(doc, "profundidade"));
                    produtoRequest.setEstoqueMinimo(getBigDecimalContent(doc, "estoqueMinimo"));
                    produtoRequest.setEstoqueMaximo(getBigDecimalContent(doc, "estoqueMaximo"));
                    produtoRequest.setEstoque(getIntegerContent(doc, "estoque"));
                    produtoRequest.setItensPorCaixa(getBigDecimalContent(doc, "itensPorCaixa"));
                    produtoRequest.setVolumes(getBigDecimalContent(doc, "volumes"));
                    produtoRequest.setUrlVideo(getTextContent(doc, "urlVideo"));

                    ImagemRequest imagemRequest = new ImagemRequest();
                    String urlDaImagem = getTextContent(doc, "url");
                    imagemRequest.setUrl(urlDaImagem);

                    produtoRequest.setImagens(imagemRequest);
                    produtoRequest.setLocalizacao(getTextContent(doc, "localizacao"));
                    produtoRequest.setCrossdocking(getBigDecimalContent(doc, "crossdocking"));
                    produtoRequest.setGarantia(getIntegerContent(doc, "garantia"));
                    produtoRequest.setSpedTipoItem(getIntegerContent(doc, "spedTipoItem"));
                    produtoRequest.setIdCategoria(getTextContent(doc, "idCategoria"));
                    produtoRequest.setFlag("PUT");

                    // Verifique se o produto existe na tabela de produtoRequest
                    String descricaoProdutoRequest = doc.getElementsByTagName("descricao").item(0).getTextContent();
                    List<ProdutoRequest> produtoRequestExistente = produtoRequestRepository.findByDescricao(descricaoProdutoRequest);
                    boolean produtoRequestExiste = !produtoRequestExistente.isEmpty();
                    //Se o produto não existe na tabela, o mesmo é cadastrado.
                    if (!produtoRequestExiste) {
                        logger.info("Produto não encontrado no Banco de dados, adicionando... [PUT]");
                        imagemRequestRepository.save(imagemRequest);
                        produtoRequestRepository.save(produtoRequest);
                    } else {
                        // Se o contato já existe, você pode atualizá-lo
                        ProdutoRequest produtoRequestAtualiza = produtoRequestExistente.get(0); // Suponhamos que haja apenas um contato com o mesmo cpf_cnpj
                        // Faça as atualizações necessárias no contato existente com base nos dados do 'contatoRequest'
                        produtoRequestAtualiza.setCodigo(produtoRequest.getCodigo());
                        produtoRequestAtualiza.setDescricao(produtoRequest.getDescricao());
                        produtoRequestAtualiza.setTipo(produtoRequest.getTipo());
                        produtoRequestAtualiza.setSituacao(produtoRequest.getSituacao());
                        produtoRequestAtualiza.setDescricaoCurta(produtoRequest.getDescricaoCurta());
                        produtoRequestAtualiza.setDescricaoComplementar(produtoRequest.getDescricaoComplementar());
                        produtoRequestAtualiza.setUn(produtoRequest.getUn());
                        produtoRequestAtualiza.setVlr_unit(produtoRequest.getVlr_unit() != null ? produtoRequest.getVlr_unit() : null);
                        produtoRequestAtualiza.setPreco_custo(produtoRequest.getPreco_custo() != null ? produtoRequest.getPreco_custo() : null);
                        produtoRequestAtualiza.setPeso_bruto(produtoRequest.getPeso_bruto() != null ? produtoRequest.getPeso_bruto() : null);
                        produtoRequestAtualiza.setPeso_liq(produtoRequest.getPeso_liq() != null ? produtoRequest.getPeso_liq() : null);
                        produtoRequestAtualiza.setClass_fiscal(produtoRequest.getClass_fiscal());
                        produtoRequestAtualiza.setMarca(produtoRequest.getMarca());
                        produtoRequestAtualiza.setCest(produtoRequest.getCest());
                        produtoRequestAtualiza.setOrigem(produtoRequest.getOrigem());
                        produtoRequestAtualiza.setIdGrupoProduto(produtoRequest.getIdGrupoProduto() != null ? produtoRequest.getIdGrupoProduto() : null);
                        produtoRequestAtualiza.setCondicao(produtoRequest.getCondicao());
                        produtoRequestAtualiza.setFreteGratis(produtoRequest.getFreteGratis());
                        produtoRequestAtualiza.setLinkExterno(produtoRequest.getLinkExterno());
                        produtoRequestAtualiza.setObservacoes(produtoRequest.getObservacoes());
                        produtoRequestAtualiza.setProducao(produtoRequest.getProducao());
                        produtoRequestAtualiza.setUnidadeMedida(produtoRequest.getUnidadeMedida());
                        produtoRequestAtualiza.setDataValidade(produtoRequest.getDataValidade());
                        produtoRequestAtualiza.setDescricaoFornecedor(produtoRequest.getDescricaoFornecedor());
                        produtoRequestAtualiza.setIdFabricante(produtoRequest.getIdFabricante() != null ? produtoRequest.getIdFabricante() : null);
                        produtoRequestAtualiza.setCodigoFabricante(produtoRequest.getCodigoFabricante());
                        produtoRequestAtualiza.setGtin(produtoRequest.getGtin());
                        produtoRequestAtualiza.setGtinEmbalagem(produtoRequest.getGtinEmbalagem());
                        produtoRequestAtualiza.setLargura(produtoRequest.getLargura());
                        produtoRequestAtualiza.setAltura(produtoRequest.getAltura());
                        produtoRequestAtualiza.setProfundidade(produtoRequest.getProfundidade());
                        produtoRequestAtualiza.setEstoqueMinimo(produtoRequest.getEstoqueMinimo() != null ? produtoRequest.getEstoqueMinimo() : null);
                        produtoRequestAtualiza.setEstoqueMaximo(produtoRequest.getEstoqueMaximo() != null ? produtoRequest.getEstoqueMaximo() : null);
                        produtoRequestAtualiza.setSpedTipoItem(produtoRequest.getSpedTipoItem() != null ? produtoRequest.getSpedTipoItem() : null);
//                        produtoRequestAtualiza.setEstoque(produtoRequest.getEstoque());
                        produtoRequestAtualiza.setItensPorCaixa(produtoRequest.getItensPorCaixa() != null ? produtoRequest.getItensPorCaixa() : null);
                        produtoRequestAtualiza.setVolumes(produtoRequest.getVolumes() != null ? produtoRequest.getVolumes() : null);
                        produtoRequestAtualiza.setUrlVideo(produtoRequest.getUrlVideo());

                        imagemRequestRepository.save(imagemRequest);
                        produtoRequestRepository.save(produtoRequestAtualiza);
                    }

                    /**
                     * ATUALIZA OS DADOS DA TABELA PRODUTO_RESPONSE POREM NO MOMENTO É REDUNDANTE TENDO EM VISTA QUE A FUNCAO GETALLPRODUCTS ATUALIZA O BANCO USANDO GET NO BLING *.
                     */
//                    ProdutoResponse produtoResponse = new ProdutoResponse();
//                    produtoResponse.setId(Long.valueOf(id));
//                    produtoResponse.setCodigo(produtoRequest.getCodigo());
//                    produtoResponse.setDescricao(produtoRequest.getDescricao());
//                    produtoResponse.setTipo(produtoRequest.getTipo());
//                    produtoResponse.setSituacao(produtoRequest.getSituacao());
//                    produtoResponse.setDescricaoCurta(produtoRequest.getDescricaoCurta());
//                    produtoResponse.setDescricaoComplementar(produtoRequest.getDescricaoComplementar());
//                    produtoResponse.setUnidade(produtoRequest.getUn());
//                    produtoResponse.setPreco(produtoRequest.getVlr_unit() != null ? produtoRequest.getVlr_unit().toString() : null);
//                    produtoResponse.setPrecoCusto(produtoRequest.getPreco_custo() != null ? produtoRequest.getPreco_custo().toString() : null);
//                    produtoResponse.setPesoBruto(produtoRequest.getPeso_bruto() != null ? produtoRequest.getPeso_bruto().toString() : null);
//                    produtoResponse.setPesoLiq(produtoRequest.getPeso_liq() != null ? produtoRequest.getPeso_liq().toString() : null);
//                    produtoResponse.setClass_fiscal(produtoRequest.getClass_fiscal());
//                    produtoResponse.setMarca(produtoRequest.getMarca());
//                    produtoResponse.setCest(produtoRequest.getCest());
//                    produtoResponse.setOrigem(produtoRequest.getOrigem());
//                    produtoResponse.setIdGrupoProduto(produtoRequest.getIdGrupoProduto() != null ? produtoRequest.getIdGrupoProduto().toString() : null);
//                    produtoResponse.setCondicao(produtoRequest.getCondicao());
//                    produtoResponse.setFreteGratis(produtoRequest.getFreteGratis());
//                    produtoResponse.setLinkExterno(produtoRequest.getLinkExterno());
//                    produtoResponse.setObservacoes(produtoRequest.getObservacoes());
//                    produtoResponse.setProducao(produtoRequest.getProducao());
//                    produtoResponse.setUnidadeMedida(produtoRequest.getUnidadeMedida());
//                    produtoResponse.setDataValidade(produtoRequest.getDataValidade());
//                    produtoResponse.setDescricaoFornecedor(produtoRequest.getDescricaoFornecedor());
//                    produtoResponse.setIdFabricante(produtoRequest.getIdFabricante() != null ? produtoRequest.getIdFabricante().toString() : null);
//                    produtoResponse.setCodigoFabricante(produtoRequest.getCodigoFabricante());
//                    produtoResponse.setGtin(produtoRequest.getGtin());
//                    produtoResponse.setGtinEmbalagem(produtoRequest.getGtinEmbalagem());
//                    produtoResponse.setLarguraProduto(produtoRequest.getLargura());
//                    produtoResponse.setAlturaProduto(produtoRequest.getAltura());
//                    produtoResponse.setProfundidadeProduto(produtoRequest.getProfundidade());
//                    produtoResponse.setEstoqueMinimo(produtoRequest.getEstoqueMinimo() != null ? produtoRequest.getEstoqueMinimo().toString() : null);
//                    produtoResponse.setEstoqueMaximo(produtoRequest.getEstoqueMaximo() != null ? produtoRequest.getEstoqueMaximo().toString() : null);
//                    produtoResponse.setItensPorCaixa(produtoRequest.getItensPorCaixa() != null ? produtoRequest.getItensPorCaixa().toString() : null);
//                    produtoResponse.setVolumes(produtoRequest.getVolumes() != null ? produtoRequest.getVolumes().toString() : null);
//                    produtoResponse.setUrlVideo(produtoRequest.getUrlVideo());
//                    produtoResponse.setImageThumbnail(produtoResponse.getImageThumbnail());
//                    produtoResponse.setLocalizacao(produtoRequest.getLocalizacao());
//                    produtoResponse.setCrossdocking(produtoRequest.getCrossdocking() != null ? produtoRequest.getCrossdocking().toString() : null);
//                    produtoResponse.setGarantia(String.valueOf(produtoRequest.getGarantia()));
//                    produtoResponse.setSpedTipoItem(String.valueOf(produtoRequest.getSpedTipoItem()));
//
//                    String descricaoProdutoResponse = doc.getElementsByTagName("descricao").item(0).getTextContent();
//                    List<ProdutoResponse> produtoResponseExistente = produtoResponseRepository.findByDescricao(descricaoProdutoResponse);
//                    boolean produtoResponseExiste = !produtoResponseExistente.isEmpty();
//
//                    // Verifica se a lista não está vazia antes de acessar o primeiro elemento
//                    if (produtoResponseExiste) {
//                        logger.info("Dados atualizados no banco de dados.");
//                        produtoResponseRepository.save(produtoResponse);
//                    }

                    return ResponseEntity.status(HttpStatus.OK).body(""); // Retorna um ResponseEntity vazio com status 200 (OK)

                } catch (NumberFormatException | ParserConfigurationException | IOException | SAXException ex) {
                    throw new ApiCategoriaException("Erro ao processar XML", ex);
                }
            } else {
                throw new ApiCategoriaException("Produto não encontrado no banco de dados", e);
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

    // Função auxiliar para obter o conteúdo de um nó de texto como BigDecimal
    public BigDecimal getBigDecimalContent(Document doc, String tagName) {
        String content = getTextContent(doc, tagName);
        return content != null ? new BigDecimal(content) : null;
    }

    /**
     * Verifica o status da API externa e atualiza o banco de dados local com os produtos cadastrados na API.
     * Este método é executado periodicamente, com o intervalo de tempo definido na propriedade "api.check.delay".
     * Se um produto existe apenas no banco de dados local, será adicionada na API do BLING.
     * Se um produto existe tanto no banco de dados local quanto no BLING, o produto será deletada do banco de dados local.
     *
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponível para a consulta.
     */
    @Scheduled(fixedDelayString = "${api.check.delay}")
    public void scheduledPostProduct() {
        try {
            logger.info("---------- Scheduled POST Produto ----------");
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/produtos/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<ProdutoRequest> produtos = produtoRequestRepository.findAll();
                List<String> codigoProdutos = produtoResponseRepository.findAllCodigo();

                if (produtos.isEmpty()) {
                    logger.info("Não há produtos no banco de dados para atualizar no Bling");
                    logger.info("--------------------------------------------");
                } else {
                    for (ProdutoRequest produto : produtos) {
                        if (produto.getFlag() != null && produto.getFlag().equals("POST")) {
                            if (!codigoProdutos.contains(produto.getCodigo())) {

                                StringBuilder xmlProdutos = new StringBuilder("<produto>");
                                //Tela Caracteristicas
                                appendIfNotNull(xmlProdutos, "codigo", produto.getCodigo());
                                appendIfNotNull(xmlProdutos, "descricao", produto.getDescricao());
                                appendIfNotNull(xmlProdutos, "tipo", produto.getTipo());
                                appendIfNotNull(xmlProdutos, "situacao", produto.getSituacao());
                                appendIfNotNull(xmlProdutos, "descricaoCurta", produto.getDescricaoCurta());
                                appendIfNotNull(xmlProdutos, "descricaoComplementar", produto.getDescricaoComplementar());
                                appendIfNotNull(xmlProdutos, "un", produto.getUn());
                                appendIfNotNull(xmlProdutos, "vlr_unit", String.valueOf(produto.getVlr_unit()));
                                appendIfNotNull(xmlProdutos, "peso_bruto", String.valueOf(produto.getPeso_bruto()));
                                appendIfNotNull(xmlProdutos, "peso_liq", String.valueOf(produto.getPeso_liq()));
                                appendIfNotNull(xmlProdutos, "condicao", produto.getCondicao());
                                appendIfNotNull(xmlProdutos, "freteGratis", produto.getFreteGratis());
                                appendIfNotNull(xmlProdutos, "linkExterno", produto.getLinkExterno());
                                appendIfNotNull(xmlProdutos, "observacoes", produto.getObservacoes());
                                appendIfNotNull(xmlProdutos, "producao", produto.getProducao());
                                appendIfNotNull(xmlProdutos, "unidadeMedida", produto.getUnidadeMedida());
                                appendIfNotNull(xmlProdutos, "dataValidade", produto.getDataValidade());
                                appendIfNotNull(xmlProdutos, "gtin", produto.getGtin());
                                appendIfNotNull(xmlProdutos, "gtinEmbalagem", produto.getGtinEmbalagem());
                                appendIfNotNull(xmlProdutos, "largura", produto.getLargura());
                                appendIfNotNull(xmlProdutos, "altura", produto.getAltura());
                                appendIfNotNull(xmlProdutos, "profundidade", produto.getProfundidade());
                                appendIfNotNull(xmlProdutos, "estoque", String.valueOf(produto.getEstoque()));
                                appendIfNotNull(xmlProdutos, "itensPorCaixa", String.valueOf(produto.getItensPorCaixa()));
                                appendIfNotNull(xmlProdutos, "urlVideo", produto.getUrlVideo());
                                //Tela Estoque
                                appendIfNotNull(xmlProdutos, "localizacao", produto.getLocalizacao());
                                appendIfNotNull(xmlProdutos, "estoqueMinimo", String.valueOf(produto.getEstoqueMinimo()));
                                appendIfNotNull(xmlProdutos, "estoqueMaximo", String.valueOf(produto.getEstoqueMaximo()));
                                appendIfNotNull(xmlProdutos, "crossdocking", String.valueOf(produto.getCrossdocking()));
                                //Tela Fornecedor
                                appendIfNotNull(xmlProdutos, "idFabricante", String.valueOf(produto.getIdFabricante()));
                                appendIfNotNull(xmlProdutos, "descricaoFornecedor", produto.getDescricaoFornecedor());
                                appendIfNotNull(xmlProdutos, "codigoFabricante", produto.getCodigoFabricante());
                                appendIfNotNull(xmlProdutos, "preco_custo", String.valueOf(produto.getPreco_custo()));
                                appendIfNotNull(xmlProdutos, "garantia", String.valueOf(produto.getGarantia()));
                                //Tela Tributacao
                                appendIfNotNull(xmlProdutos, "class_fiscal", produto.getClass_fiscal());
                                appendIfNotNull(xmlProdutos, "marca", produto.getMarca());
                                appendIfNotNull(xmlProdutos, "cest", produto.getCest());
                                appendIfNotNull(xmlProdutos, "origem", produto.getOrigem());
                                appendIfNotNull(xmlProdutos, "idGrupoProduto", String.valueOf(produto.getIdGrupoProduto()));
                                appendIfNotNull(xmlProdutos, "spedTipoItem", String.valueOf(produto.getSpedTipoItem()));
                                //Tela Imagens
                                xmlProdutos.append("<imagens>");
                                appendIfNotNull(xmlProdutos, "url", String.valueOf(produto.getImagens().getUrl()));
                                xmlProdutos.append("</imagens>");
                                appendIfNotNull(xmlProdutos, "idCategoria", String.valueOf(produto.getIdCategoria()));
                                xmlProdutos.append("</produto>");

                                // Chama a função updateProduct e armazena o valor de HttpStatus.
                                ResponseEntity<String> createResponse = createProduct(xmlProdutos.toString());
                                ;

                                if (createResponse.getStatusCode() == HttpStatus.CREATED) {
                                    produtoRequestRepository.delete(produto);
                                    imagemRequestRepository.delete(produto.getImagens());
                                }
                                logger.info("--------------------------------------------");
                            } else {
                                logger.info("Produto já existe no banco de dados, deletando...");
                                produtoRequestRepository.delete(produto);
                                imagemRequestRepository.delete(produto.getImagens());
                            }
                        }
                        logger.info("--------------------------------------------");
                    }
                }
            } else {
                logger.info(" ERRO na função scheduledPostProduct [POST]");
            }
        } catch (RestClientException e) {
            logger.info("API Bling Produto está offline, nada a fazer");
        }
    }

    public void appendIfNotNull(StringBuilder xml, String tagName, String value) {
        if (value != null && !value.equals("null")) {
            xml.append("<").append(tagName).append(">").append(value).append("</").append(tagName).append(">");
        }
    }

    /**
     * Verifica o status da API externa e atualiza o banco de dados local com os produtos na API.
     * Este método é executado periodicamente, com o intervalo de tempo definido na propriedade "api.check.delay".
     * Se uma categoria existe apenas no banco de dados local, ela será adicionada na API.
     * Se uma categoria existe tanto no banco de dados local quanto na API, ela será deletada do banco de dados local.
     *
     * @throws ApiCategoriaException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponível para a consulta.
     */
    @Scheduled(fixedDelayString = "${api.check.delay}")
    public void scheduledUpdateProduct() {
        try {
            logger.info("---------- Scheduled PUT Produto -----------");
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/produtos/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<ProdutoRequest> produtoRequests = produtoRequestRepository.findAll();

                if (produtoRequests.isEmpty()) {
                    logger.info("Não há produtos no banco de dados para atualizar no Bling");
                    logger.info("--------------------------------------------");
                } else {
                    for (ProdutoRequest produto : produtoRequests) {
                        if ("PUT".equals(produto.getFlag())) { // verifica se a flag é "PUT"
                            String codigo = String.valueOf(produto.getCodigo());

                            StringBuilder xmlProdutos = new StringBuilder("<produto>");
                            //Tela Caracteristicas
                            appendIfNotNull(xmlProdutos, "codigo", produto.getCodigo());
                            appendIfNotNull(xmlProdutos, "descricao", produto.getDescricao());
                            appendIfNotNull(xmlProdutos, "tipo", produto.getTipo());
                            appendIfNotNull(xmlProdutos, "situacao", produto.getSituacao());
                            appendIfNotNull(xmlProdutos, "descricaoCurta", produto.getDescricaoCurta());
                            appendIfNotNull(xmlProdutos, "descricaoComplementar", produto.getDescricaoComplementar());
                            appendIfNotNull(xmlProdutos, "un", produto.getUn());
                            appendIfNotNull(xmlProdutos, "vlr_unit", String.valueOf(produto.getVlr_unit()));
                            appendIfNotNull(xmlProdutos, "peso_bruto", String.valueOf(produto.getPeso_bruto()));
                            appendIfNotNull(xmlProdutos, "peso_liq", String.valueOf(produto.getPeso_liq()));
                            appendIfNotNull(xmlProdutos, "condicao", produto.getCondicao());
                            appendIfNotNull(xmlProdutos, "freteGratis", produto.getFreteGratis());
                            appendIfNotNull(xmlProdutos, "linkExterno", produto.getLinkExterno());
                            appendIfNotNull(xmlProdutos, "observacoes", produto.getObservacoes());
                            appendIfNotNull(xmlProdutos, "producao", produto.getProducao());
                            appendIfNotNull(xmlProdutos, "unidadeMedida", produto.getUnidadeMedida());
                            appendIfNotNull(xmlProdutos, "dataValidade", produto.getDataValidade());
                            appendIfNotNull(xmlProdutos, "gtin", produto.getGtin());
                            appendIfNotNull(xmlProdutos, "gtinEmbalagem", produto.getGtinEmbalagem());
                            appendIfNotNull(xmlProdutos, "largura", produto.getLargura());
                            appendIfNotNull(xmlProdutos, "altura", produto.getAltura());
                            appendIfNotNull(xmlProdutos, "profundidade", produto.getProfundidade());
                            appendIfNotNull(xmlProdutos, "estoque", String.valueOf(produto.getEstoque()));
                            appendIfNotNull(xmlProdutos, "itensPorCaixa", String.valueOf(produto.getItensPorCaixa()));
                            appendIfNotNull(xmlProdutos, "urlVideo", produto.getUrlVideo());
                            //Tela Estoque
                            appendIfNotNull(xmlProdutos, "localizacao", produto.getLocalizacao());
                            appendIfNotNull(xmlProdutos, "estoqueMinimo", String.valueOf(produto.getEstoqueMinimo()));
                            appendIfNotNull(xmlProdutos, "estoqueMaximo", String.valueOf(produto.getEstoqueMaximo()));
                            appendIfNotNull(xmlProdutos, "crossdocking", String.valueOf(produto.getCrossdocking()));
                            //Tela Fornecedor
                            appendIfNotNull(xmlProdutos, "idFabricante", String.valueOf(produto.getIdFabricante()));
                            appendIfNotNull(xmlProdutos, "descricaoFornecedor", produto.getDescricaoFornecedor());
                            appendIfNotNull(xmlProdutos, "codigoFabricante", produto.getCodigoFabricante());
                            appendIfNotNull(xmlProdutos, "preco_custo", String.valueOf(produto.getPreco_custo()));
                            appendIfNotNull(xmlProdutos, "garantia", String.valueOf(produto.getGarantia()));
                            //Tela Tributacao
                            appendIfNotNull(xmlProdutos, "class_fiscal", produto.getClass_fiscal());
                            appendIfNotNull(xmlProdutos, "marca", produto.getMarca());
                            appendIfNotNull(xmlProdutos, "cest", produto.getCest());
                            appendIfNotNull(xmlProdutos, "origem", produto.getOrigem());
                            appendIfNotNull(xmlProdutos, "idGrupoProduto", String.valueOf(produto.getIdGrupoProduto()));
                            appendIfNotNull(xmlProdutos, "spedTipoItem", String.valueOf(produto.getSpedTipoItem()));
                            //Tela Imagens
                            xmlProdutos.append("<imagens>");
                            appendIfNotNull(xmlProdutos, "url", String.valueOf(produto.getImagens().getUrl()));
                            xmlProdutos.append("</imagens>");
                            appendIfNotNull(xmlProdutos, "idCategoria", String.valueOf(produto.getIdCategoria()));
                            xmlProdutos.append("</produto>");

                            // Chama a função updateProduct e armazena o valor de HttpStatus.
                            ResponseEntity<String> createResponse = updateProduct(xmlProdutos.toString(), codigo);

                            if (createResponse.getStatusCode() == HttpStatus.CREATED) {
                                produtoRequestRepository.delete(produto);
                                imagemRequestRepository.delete(produto.getImagens());
                            }
                            logger.info("--------------------------------------------");
                        }
                    }
                }
            } else {
                logger.info("ERRO na função scheduledUpdateProduct [PUT]");
            }
        } catch (RestClientException e) {
            logger.info("API Bling Produto está offline, nada a fazer");
        }
    }

    /**
     * Verifica o status da API externa e atualiza o banco de dados local com os produtos deletados na API.
     * Este método é executado periodicamente, com o intervalo de tempo definido na propriedade "api.check.delay".
     * Se um produto existe apenas no banco de dados local, será adicionada na API do BLING.
     * Se um produto existe tanto no banco de dados local quanto no BLING, o produto será deletada do banco de dados local.
     *
     * @throws ApiProdutoException Caso ocorra algum erro na comunicação com a API externa o banco de dados fica disponível para a consulta.
     */
    @Scheduled(fixedDelayString = "${api.check.delay}")
    public void scheduledDeleteProduct() {
        try {
            logger.info("--------- Scheduled DELETE Produto ---------");
//            String url = "http://www.teste.com/";
            String url = apiBaseUrl + "/produtos/json/" + apikeyparam + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<ProdutoRequest> produtoRequests = produtoRequestRepository.findAll();

                if (produtoRequests.isEmpty()) {
                    logger.info("Não há produtos no banco de dados para atualizar no Bling");
                    logger.info("--------------------------------------------");
                } else {
                    for (ProdutoRequest produto : produtoRequests) {
                        if ("DELETE".equals(produto.getFlag())) { // verifica se a flag é "DELETE".
                            String codigo = String.valueOf(produto.getCodigo());

                            // Chama a função deleteProductByCode e armazena o valor de HttpStatus.
                            ResponseEntity<String> createResponse = deleteProductByCode(codigo);

                            if (createResponse.getStatusCode() == HttpStatus.OK) {
                                produtoRequestRepository.delete(produto);
                            }
                            logger.info("--------------------------------------------");
                        }
                    }
                }
            } else {
                logger.info("ERRO na função scheduledDeleteProduct [DELETE]");
            }
        } catch (RestClientException e) {
            logger.info("API Bling Produto está offline, nada a fazer");
        }
    }


    /**
     * ---------------------------------------------------- VERSÃO 1 - SEM CONEXÃO AO BANCO DE DADOS. ----------------------------------------------------------
     */

    /**
     * GET "BUSCAR A LISTA DE PRODUTOS CADASTRADO NO BLING".
     */
//    @Override
//    public JsonResponse getAllProducts() throws ApiProdutoException {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(headers);
//
//            String url = apiBaseUrl + "/produtos/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonResponse result = objectMapper.readValue(response.getBody(), JsonResponse.class);
//
//            return produtoRepository.save(result);
//
//        } catch (JsonProcessingException e) {
//            throw new ApiProdutoException("Erro ao processar JSON", e);
//        } catch (RestClientException e) {
//            throw new ApiProdutoException("Erro ao chamar API", e);
//        }
//    }

    /**
     * GET "BUSCAR UM PRODUTO PELO CODIGO (SKU) E IDFORNECEDOR".
     */
//    @Override
//    public JsonResponse getProductByCodeSupplier(String codigoFabricante, String idFabricante) throws ApiProdutoException {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(headers);
//
//            String url = apiBaseUrl + "/produto/" + codigoFabricante + "/" + idFabricante + "/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonResponse result = objectMapper.readValue(response.getBody(), JsonResponse.class);
//
//            return result;
//
//        } catch (JsonProcessingException e) {
//            throw new ApiProdutoException("Erro ao processar JSON", e);
//        } catch (RestClientException e) {
//            throw new ApiProdutoException("Erro ao chamar API", e);
//        }
//    }

    /**
     * DELETE "DELETA UM PRODUTO PELO CODIGO (SKU)".
     */
//    @Override
//    public void deleteProductByCode(String codigo) throws ApiProdutoException {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> request = new HttpEntity<>(headers);
//
//            String url = apiBaseUrl + "/produto/" + codigo + "/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
//
//        } catch (RestClientException e) {
//            throw new ApiProdutoException("Erro ao chamar API", e);
//        }
//    }

    /**
     * SCHEDULED POST "INSERE UM PRODUTO CADASTRADO DE FORMA OFFLINE NA PLATAFORMA BLING".
     */
//    @Scheduled(fixedDelayString = "${api.check.delay}")
//    public void scheduledPostCategory() {
//        try {
//            logger.info("Chamei o Scheduled POST");
////            String url = "http://www.teste.com/";
//            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                List<ProdutoRequest> produtos = produtoRequestRepository.findAll();
//                List<String> codigoProdutos = produtoResponseRepository.findAllCodigo();
//
//                for (ProdutoRequest produto : produtos) {
//                    if (produto.getFlag() != null && produto.getFlag().equals("POST")) {
//                        if (!codigoProdutos.contains(produto.getCodigo())) {
//                            logger.info("Produto não encontrada na API, adicionando...");
//                            String xmlProdutos = "<produto>";
//                            xmlProdutos += "<codigo>" + produto.getCodigo() + "</codigo>";
//                            xmlProdutos += "<descricao>" + produto.getDescricao() + "</descricao>";
//                            xmlProdutos += "<tipo>" + produto.getTipo() + "</tipo>";
//                            xmlProdutos += "<situacao>" + produto.getSituacao() + "</situacao>";
//                            xmlProdutos += "<descricaoCurta>" + produto.getDescricaoCurta() + "</descricaoCurta>";
//                            xmlProdutos += "<descricaoComplementar>" + produto.getDescricaoComplementar() + "</descricaoComplementar>";
//                            xmlProdutos += "<un>" + produto.getUn() + "</un>";
//                            xmlProdutos += "<vlr_unit>" + produto.getVlr_unit() + "</vlr_unit>";
//                            xmlProdutos += "<preco_custo>" + produto.getPreco_custo() + "</preco_custo>";
//                            xmlProdutos += "<peso_bruto>" + produto.getPeso_bruto() + "</peso_bruto>";
//                            xmlProdutos += "<peso_liq>" + produto.getPeso_liq() + "</peso_liq>";
//                            xmlProdutos += "<class_fiscal>" + produto.getClass_fiscal() + "</class_fiscal>";
//                            xmlProdutos += "<marca>" + produto.getMarca() + "</marca>";
//                            xmlProdutos += "<cest>" + produto.getCest() + "</cest>";
//                            xmlProdutos += "<origem>" + produto.getOrigem() + "</origem>";
//                            xmlProdutos += "<idGrupoProduto>" + produto.getIdGrupoProduto() + "</idGrupoProduto>";
//                            xmlProdutos += "<condicao>" + produto.getCondicao() + "</condicao>";
//                            xmlProdutos += "<freteGratis>" + produto.getFreteGratis() + "</freteGratis>";
//                            xmlProdutos += "<linkExterno>" + produto.getLinkExterno() + "</linkExterno>";
//                            xmlProdutos += "<observacoes>" + produto.getObservacoes() + "</observacoes>";
//                            xmlProdutos += "<producao>" + produto.getProducao() + "</producao>";
//                            xmlProdutos += "<unidadeMedida>" + produto.getUnidadeMedida() + "</unidadeMedida>";
//                            xmlProdutos += "<dataValidade>" + produto.getDataValidade() + "</dataValidade>";
//                            xmlProdutos += "<descricaoFornecedor>" + produto.getDescricaoFornecedor() + "</descricaoFornecedor>";
//                            xmlProdutos += "<idFabricante>" + produto.getIdFabricante() + "</idFabricante>";
//                            xmlProdutos += "<codigoFabricante>" + produto.getCodigoFabricante() + "</codigoFabricante>";
//                            xmlProdutos += "<gtin>" + produto.getGtin() + "</gtin>";
//                            xmlProdutos += "<gtinEmbalagem>" + produto.getGtinEmbalagem() + "</gtinEmbalagem>";
//                            xmlProdutos += "<largura>" + produto.getLargura() + "</largura>";
//                            xmlProdutos += "<altura>" + produto.getAltura() + "</altura>";
//                            xmlProdutos += "<profundidade>" + produto.getProfundidade() + "</profundidade>";
//                            xmlProdutos += "<estoqueMinimo>" + produto.getEstoqueMinimo() + "</estoqueMinimo>";
//                            xmlProdutos += "<estoqueMaximo>" + produto.getEstoqueMaximo() + "</estoqueMaximo>";
//                            xmlProdutos += "<estoque>" + produto.getEstoque() + "</estoque>";
//                            xmlProdutos += "<itensPorCaixa>" + produto.getItensPorCaixa() + "</itensPorCaixa>";
//                            xmlProdutos += "<urlVideo>" + produto.getUrlVideo() + "</urlVideo>";
//                            xmlProdutos += "<localizacao>" + produto.getLocalizacao() + "</localizacao>";
//                            xmlProdutos += "<crossdocking>" + produto.getCrossdocking() + "</crossdocking>";
//                            xmlProdutos += "<garantia>" + produto.getGarantia() + "</garantia>";
//                            xmlProdutos += "<spedTipoItem>" + produto.getSpedTipoItem() + "</spedTipoItem>";
//                            xmlProdutos += "<idCategoria>" + produto.getIdCategoria() + "</idCategoria>";
//                            xmlProdutos += "</produto>";
//
//                            createProduct(xmlProdutos);
//                            produtoRequestRepository.delete(produto);
//                        } else {
//                            logger.info("Categoria já existe na API, deletando...");
//                            produtoRequestRepository.delete(produto);
//                        }
//                    }
//                }
//            }
//        } catch (RestClientException e) {
//            logger.info("API está offline, nada a fazer");
//        }
//    }

//    @Scheduled(fixedDelayString = "${api.check.delay}")
//    public void scheduledUpdateCategory() {
//        try {
//            logger.info("Chamei o Scheduled PUT");
////            String url = "http://www.teste.com/";
//            String url = apiBaseUrl + "/categorias/json/" + apikeyparam + apiKey;
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                List<CategoriaRequest> categoriaRequests = categoriaRequestRepository.findAll();
//
//                for (CategoriaRequest categoriaRequest : categoriaRequests) {
//                    if ("PUT".equals(categoriaRequest.getFlag())) { // verifica se a flag é "PUT"
//                        String xmlCategoria = "<categorias>";
//                        xmlCategoria += "<categoria>";
//                        xmlCategoria += "<descricao>" + categoriaRequest.getDescricao() + "</descricao>";
//                        xmlCategoria += "<idCategoriaPai>" + categoriaRequest.getIdCategoriaPai() + "</idCategoriaPai>";
//                        xmlCategoria += "</categoria>";
//                        xmlCategoria += "</categorias>";
//
//                        String idCategoria = String.valueOf(categoriaRequest.getId());
//
//                        updateCategory(xmlCategoria, idCategoria);
//
//                        categoriaRequestRepository.delete(categoriaRequest);
//                    }
//                }
//            }
//        } catch (RestClientException e) {
//            logger.info("API está offline, nada a fazer");
//        }
//    }

/**
 *   VERSAO 01 DO ARMAZENAMENTO AO BANCO DE DADOS NO MÉTODO createProduct(String xmlProdutos)
 */
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                InputSource is = new InputSource(new StringReader(xmlProdutos));
//                Document doc = builder.parse(is);
//
//                ProdutoRequest produtoRequest = new ProdutoRequest();
//
//                Node nodeProduto = doc.getElementsByTagName("produto").item(0);
//                Element elementoProduto = (Element) nodeProduto;
//
//                Node codigoNode = elementoProduto.getElementsByTagName("codigo").item(0);
//                String codigo = codigoNode != null && codigoNode.getTextContent() != null ? codigoNode.getTextContent() : null;
//                produtoRequest.setCodigo(codigo);
//
//                Node descricaoNode = elementoProduto.getElementsByTagName("descricao").item(0);
//                String descricao = descricaoNode != null && descricaoNode.getTextContent() != null ? descricaoNode.getTextContent() : null;
//                produtoRequest.setDescricao(descricao);
//
//                Node tipoNode = elementoProduto.getElementsByTagName("tipo").item(0);
//                String tipo = tipoNode != null && tipoNode.getTextContent() != null ? tipoNode.getTextContent() : null;
//                produtoRequest.setTipo(tipo);
//
//                Node situacaoNode = elementoProduto.getElementsByTagName("situacao").item(0);
//                String situacao = situacaoNode != null && situacaoNode.getTextContent() != null ? situacaoNode.getTextContent() : null;
//                produtoRequest.setSituacao(situacao);
//
//                Node descricaoCurtaNode = elementoProduto.getElementsByTagName("descricaoCurta").item(0);
//                String descricaoCurta = descricaoCurtaNode != null && descricaoCurtaNode.getTextContent() != null ? descricaoCurtaNode.getTextContent() : null;
//                produtoRequest.setDescricaoCurta(descricaoCurta);
//
//                Node descricaoComplementarNode = elementoProduto.getElementsByTagName("descricaoComplementar").item(0);
//                String descricaoComplementar = descricaoComplementarNode != null && descricaoComplementarNode.getTextContent() != null ? descricaoComplementarNode.getTextContent() : null;
//                produtoRequest.setDescricaoComplementar(descricaoComplementar);
//
//                Node unNode = elementoProduto.getElementsByTagName("un").item(0);
//                String un = unNode != null && unNode.getTextContent() != null ? unNode.getTextContent() : null;
//                produtoRequest.setUn(un);
//
//                Node vlrUnitNode = elementoProduto.getElementsByTagName("vlr_unit").item(0);
//                BigDecimal vlr_unit = vlrUnitNode != null && vlrUnitNode.getTextContent() != null ? new BigDecimal(vlrUnitNode.getTextContent()) : null;
//                produtoRequest.setVlr_unit(vlr_unit);
//
//                Node vlrDescNode = elementoProduto.getElementsByTagName("vlr_desc").item(0);
//                BigDecimal vlr_desc = vlrDescNode != null && vlrDescNode.getTextContent() != null ? new BigDecimal(vlrDescNode.getTextContent()) : null;
//                produtoRequest.setVlr_desc(vlr_desc);
//
//                Node precoCustoNode = elementoProduto.getElementsByTagName("preco_custo").item(0);
//                BigDecimal preco_custo = precoCustoNode != null && precoCustoNode.getTextContent() != null ? new BigDecimal(precoCustoNode.getTextContent()) : null;
//                produtoRequest.setPreco_custo(preco_custo);
//
//                Node pesoBrutoNode = elementoProduto.getElementsByTagName("peso_bruto").item(0);
//                BigDecimal peso_bruto = pesoBrutoNode != null && pesoBrutoNode.getTextContent() != null ? new BigDecimal(pesoBrutoNode.getTextContent()) : null;
//                produtoRequest.setPeso_bruto(peso_bruto);
//
//                Node pesoLiqNode = elementoProduto.getElementsByTagName("peso_liq").item(0);
//                BigDecimal peso_liq = pesoLiqNode != null && pesoLiqNode.getTextContent() != null ? new BigDecimal(pesoLiqNode.getTextContent()) : null;
//                produtoRequest.setPeso_liq(peso_liq);
//
//                Node classFiscalNode = elementoProduto.getElementsByTagName("class_fiscal").item(0);
//                String classFiscal = classFiscalNode != null && classFiscalNode.getTextContent() != null ? classFiscalNode.getTextContent() : null;
//                produtoRequest.setClass_fiscal(classFiscal);
//
//                Node marcaNode = elementoProduto.getElementsByTagName("marca").item(0);
//                String marca = marcaNode != null && marcaNode.getTextContent() != null ? marcaNode.getTextContent() : null;
//                produtoRequest.setMarca(marca);
//
//                Node cestNode = elementoProduto.getElementsByTagName("cest").item(0);
//                String cest = cestNode != null && cestNode.getTextContent() != null ? cestNode.getTextContent() : null;
//                produtoRequest.setCest(cest);
//
//                Node origemNode = elementoProduto.getElementsByTagName("origem").item(0);
//                String origem = origemNode != null && origemNode.getTextContent() != null ? origemNode.getTextContent() : null;
//                produtoRequest.setOrigem(origem);
//
//                Node idGrupoProdutoNode = elementoProduto.getElementsByTagName("idGrupoProduto").item(0);
//                BigDecimal idGrupoProduto = idGrupoProdutoNode != null && idGrupoProdutoNode.getTextContent() != null ? new BigDecimal(idGrupoProdutoNode.getTextContent()) : null;
//                produtoRequest.setIdGrupoProduto(idGrupoProduto);
//
//                Node condicaoNode = elementoProduto.getElementsByTagName("condicao").item(0);
//                String condicao = condicaoNode != null && condicaoNode.getTextContent() != null ? condicaoNode.getTextContent() : null;
//                produtoRequest.setCondicao(condicao);
//
//                Node freteGratisNode = elementoProduto.getElementsByTagName("freteGratis").item(0);
//                String freteGratis = freteGratisNode != null && freteGratisNode.getTextContent() != null ? freteGratisNode.getTextContent() : null;
//                produtoRequest.setFreteGratis(freteGratis);
//
//                Node linkExternoNode = elementoProduto.getElementsByTagName("linkExterno").item(0);
//                String linkExterno = linkExternoNode != null && linkExternoNode.getTextContent() != null ? linkExternoNode.getTextContent() : null;
//                produtoRequest.setLinkExterno(linkExterno);
//
//                Node observacoesNode = elementoProduto.getElementsByTagName("observacoes").item(0);
//                String observacoes = observacoesNode != null && observacoesNode.getTextContent() != null ? observacoesNode.getTextContent() : null;
//                produtoRequest.setObservacoes(observacoes);
//
//                Node producaoNode = elementoProduto.getElementsByTagName("producao").item(0);
//                String producao = producaoNode != null && producaoNode.getTextContent() != null ? producaoNode.getTextContent() : null;
//                produtoRequest.setProducao(producao);
//
//                Node unidadeMedidaNode = elementoProduto.getElementsByTagName("unidadeMedida").item(0);
//                String unidadeMedida = unidadeMedidaNode != null && unidadeMedidaNode.getTextContent() != null ? unidadeMedidaNode.getTextContent() : null;
//                produtoRequest.setUnidadeMedida(unidadeMedida);
//
//                Node dataValidadeNode = elementoProduto.getElementsByTagName("dataValidade").item(0);
//                String dataValidade = dataValidadeNode != null && dataValidadeNode.getTextContent() != null ? dataValidadeNode.getTextContent() : null;
//                produtoRequest.setDataValidade(dataValidade);
//
//                Node descricaoFornecedorNode = elementoProduto.getElementsByTagName("descricaoFornecedor").item(0);
//                String descricaoFornecedor = descricaoFornecedorNode != null && descricaoFornecedorNode.getTextContent() != null ? descricaoFornecedorNode.getTextContent() : null;
//                produtoRequest.setDescricaoFornecedor(descricaoFornecedor);
//
//                Node idFabricanteNode = elementoProduto.getElementsByTagName("idFabricante").item(0);
//                BigDecimal idFabricante = idFabricanteNode != null && idFabricanteNode.getTextContent() != null ? new BigDecimal(idFabricanteNode.getTextContent()) : null;
//                produtoRequest.setIdFabricante(idFabricante);
//
//                Node codigoFabricanteNode = elementoProduto.getElementsByTagName("codigoFabricante").item(0);
//                String codigoFabricante = codigoFabricanteNode != null && codigoFabricanteNode.getTextContent() != null ? codigoFabricanteNode.getTextContent() : null;
//                produtoRequest.setCodigoFabricante(codigoFabricante);
//
//                Node gtinNode = elementoProduto.getElementsByTagName("gtin").item(0);
//                String gtin = gtinNode != null && gtinNode.getTextContent() != null ? gtinNode.getTextContent() : null;
//                produtoRequest.setGtin(gtin);
//
//                Node gtinEmbalagemNode = elementoProduto.getElementsByTagName("gtinEmbalagem").item(0);
//                String gtinEmbalagem = gtinEmbalagemNode != null && gtinEmbalagemNode.getTextContent() != null ? gtinEmbalagemNode.getTextContent() : null;
//                produtoRequest.setGtinEmbalagem(gtinEmbalagem);
//
//                Node larguraNode = elementoProduto.getElementsByTagName("largura").item(0);
//                String largura = larguraNode != null && larguraNode.getTextContent() != null ? larguraNode.getTextContent() : null;
//                produtoRequest.setLargura(largura);
//
//                Node alturaNode = elementoProduto.getElementsByTagName("altura").item(0);
//                String altura = alturaNode != null && alturaNode.getTextContent() != null ? alturaNode.getTextContent() : null;
//                produtoRequest.setAltura(altura);
//
//                Node profundidadeNode = elementoProduto.getElementsByTagName("profundidade").item(0);
//                String profundidade = profundidadeNode != null && profundidadeNode.getTextContent() != null ? profundidadeNode.getTextContent() : null;
//                produtoRequest.setProfundidade(profundidade);
//
//                Node estoqueMinimoNode = elementoProduto.getElementsByTagName("estoqueMinimo").item(0);
//                BigDecimal estoqueMinimo = estoqueMinimoNode != null && estoqueMinimoNode.getTextContent() != null ? new BigDecimal(estoqueMinimoNode.getTextContent()) : null;
//                produtoRequest.setEstoqueMinimo(estoqueMinimo);
//
//                Node estoqueMaximoNode = elementoProduto.getElementsByTagName("estoqueMaximo").item(0);
//                BigDecimal estoqueMaximo = estoqueMaximoNode != null && estoqueMaximoNode.getTextContent() != null ? new BigDecimal(estoqueMaximoNode.getTextContent()) : null;
//                produtoRequest.setEstoqueMaximo(estoqueMaximo);
//
//                Node estoqueNode = elementoProduto.getElementsByTagName("estoque").item(0);
//                Integer estoque = estoqueNode != null && estoqueNode.getTextContent() != null ? Integer.parseInt(estoqueNode.getTextContent()) : null;
//                produtoRequest.setEstoque(estoque);
//
//                Node itensPorCaixaNode = elementoProduto.getElementsByTagName("itensPorCaixa").item(0);
//                BigDecimal itensPorCaixa = itensPorCaixaNode != null && itensPorCaixaNode.getTextContent() != null ? new BigDecimal(itensPorCaixaNode.getTextContent()) : null;
//                produtoRequest.setItensPorCaixa(itensPorCaixa);
//
//                Node volumesNode = elementoProduto.getElementsByTagName("volumes").item(0);
//                BigDecimal volumes = volumesNode != null && volumesNode.getTextContent() != null ? new BigDecimal(volumesNode.getTextContent()) : null;
//                produtoRequest.setVolumes(volumes);
//
//                Node urlVideoNode = elementoProduto.getElementsByTagName("urlVideo").item(0);
//                String urlVideo = urlVideoNode != null && urlVideoNode.getTextContent() != null ? urlVideoNode.getTextContent() : null;
//                produtoRequest.setUrlVideo(urlVideo);
//
//                Node localizacaoNode = elementoProduto.getElementsByTagName("localizacao").item(0);
//                String localizacao = localizacaoNode != null && localizacaoNode.getTextContent() != null ? localizacaoNode.getTextContent() : null;
//                produtoRequest.setLocalizacao(localizacao);
//
//                Node crossdockingNode = elementoProduto.getElementsByTagName("crossdocking").item(0);
//                BigDecimal crossdocking = crossdockingNode != null && crossdockingNode.getTextContent() != null ? new BigDecimal(crossdockingNode.getTextContent()) : null;
//                produtoRequest.setCrossdocking(crossdocking);
//
//                Node garantiaNode = elementoProduto.getElementsByTagName("garantia").item(0);
//                Integer garantia = garantiaNode != null && garantiaNode.getTextContent() != null ? Integer.parseInt(garantiaNode.getTextContent()) : null;
//                produtoRequest.setGarantia(garantia);
//
//                Node spedTipoItemNode = elementoProduto.getElementsByTagName("spedTipoItem").item(0);
//                Integer spedTipoItem = spedTipoItemNode != null && spedTipoItemNode.getTextContent() != null ? Integer.parseInt(spedTipoItemNode.getTextContent()) : null;
//                produtoRequest.setSpedTipoItem(spedTipoItem);
//
//                Node idCategoriaNode = elementoProduto.getElementsByTagName("idCategoria").item(0);
//                String idCategoria = idCategoriaNode != null && idCategoriaNode.getTextContent() != null ? idCategoriaNode.getTextContent() : null;
//                produtoRequest.setIdCategoria(idCategoria);
//
//                produtoRequest.setFlag("POST");
//
//                // Verifique se o produto já existe no banco de dados
//                String descricaoProduto = elementoProduto.getElementsByTagName("descricao").item(0).getTextContent();
//                List<ProdutoRequest> produtoExistente = produtoRequestRepository.findByDescricao(descricaoProduto);
//                boolean produtoJaExiste = !produtoExistente.isEmpty();
//
//                if (!produtoJaExiste) {
//                    produtoRequestRepository.save(produtoRequest);
//                }

/**
 *      VERSAO 01 DO ARMAZENAMENTO AO BANCO DE DADOS NO MÉTODO updateProduct(String xmlProdutos, String codigo)
 */
//                    //Adiciona na tabela tb_categoria_request a categoria atualizada e adiciona uma flaf PUT para posterior ser atualizado.
//                    ProdutoRequest produtoRequest = new ProdutoRequest();
//
//                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                    DocumentBuilder builder = factory.newDocumentBuilder();
//                    InputSource is = new InputSource(new StringReader(xmlProdutos));
//                    Document doc = builder.parse(is);
//
//                    NodeList idNodes = doc.getElementsByTagName("id");
//                    Long id = null;
//
//                    if (idNodes != null && idNodes.getLength() > 0) {
//                        String idString = idNodes.item(0).getTextContent().trim();
//                        if (!"null".equalsIgnoreCase(idString)) {
//                            id = Long.valueOf(idString);
//                            produtoRequest.setId(id);
//                        }
//                    }
//
//                    NodeList codigoNodes = doc.getElementsByTagName("codigo");
//                    String codigoProduto = "";
//                    if (codigoNodes != null && codigoNodes.getLength() > 0) {
//                        codigoProduto = codigoNodes.item(0).getTextContent();
//                        produtoRequest.setCodigo(codigoProduto);
//                    }
//
//                    NodeList codigoItemNodes = doc.getElementsByTagName("codigoItem");
//                    Integer codigoItem = null;
//                    if (codigoItemNodes != null && codigoItemNodes.getLength() > 0) {
//                        codigoItem = Integer.valueOf(codigoItemNodes.item(0).getTextContent());
//                        produtoRequest.setCodigoItem(Integer.valueOf(codigoItem));
//                    }
//
//                    NodeList descricaoNodes = doc.getElementsByTagName("descricao");
//                    String descricao = "";
//                    if (descricaoNodes != null && descricaoNodes.getLength() > 0) {
//                        descricao = descricaoNodes.item(0).getTextContent();
//                        produtoRequest.setDescricao(descricao);
//                    }
//
//                    NodeList tipoNodes = doc.getElementsByTagName("tipo");
//                    String tipo = "";
//                    if (tipoNodes != null && tipoNodes.getLength() > 0) {
//                        tipo = tipoNodes.item(0).getTextContent();
//                        produtoRequest.setTipo(tipo);
//                    }
//
//                    NodeList situacaoNodes = doc.getElementsByTagName("situacao");
//                    String situacao = "";
//                    if (situacaoNodes != null && situacaoNodes.getLength() > 0) {
//                        situacao = situacaoNodes.item(0).getTextContent();
//                        produtoRequest.setSituacao(situacao);
//                    }
//
//                    NodeList descricaoCurtaNodes = doc.getElementsByTagName("descricaoCurta");
//                    String descricaoCurta = "";
//                    if (descricaoCurtaNodes != null && descricaoCurtaNodes.getLength() > 0) {
//                        descricaoCurta = descricaoCurtaNodes.item(0).getTextContent();
//                        produtoRequest.setDescricaoCurta(descricaoCurta);
//                    }
//
//                    NodeList descricaoComplementarNodes = doc.getElementsByTagName("descricaoComplementar");
//                    String descricaoComplementar = "";
//                    if (descricaoComplementarNodes != null && descricaoComplementarNodes.getLength() > 0) {
//                        descricaoComplementar = descricaoComplementarNodes.item(0).getTextContent();
//                        produtoRequest.setDescricaoComplementar(descricaoComplementar);
//                    }
//
//                    NodeList unNodes = doc.getElementsByTagName("un");
//                    String un = "";
//                    if (unNodes != null && unNodes.getLength() > 0) {
//                        un = unNodes.item(0).getTextContent();
//                        produtoRequest.setUn(un);
//                    }
//
//                    NodeList vlr_unitNodes = doc.getElementsByTagName("vlr_unit");
//                    BigDecimal vlr_unit = null;
//                    if (vlr_unitNodes != null && vlr_unitNodes.getLength() > 0) {
//                        String vlr_unitString = vlr_unitNodes.item(0).getTextContent();
//                        vlr_unit = new BigDecimal(vlr_unitString);
//                        produtoRequest.setVlr_unit(vlr_unit);
//                    }
//
//                    NodeList vlr_descNodes = doc.getElementsByTagName("vlr_desc");
//                    BigDecimal vlr_desc = null;
//                    if (vlr_descNodes != null && vlr_descNodes.getLength() > 0) {
//                        String vlr_unitString = vlr_descNodes.item(0).getTextContent();
//                        vlr_desc = new BigDecimal(vlr_unitString);
//                        produtoRequest.setVlr_desc(vlr_desc);
//                    }
//
//                    NodeList preco_custoNodes = doc.getElementsByTagName("preco_custo");
//                    BigDecimal preco_custo = null;
//                    if (preco_custoNodes != null && preco_custoNodes.getLength() > 0) {
//                        String preco_custoString = preco_custoNodes.item(0).getTextContent().trim();
//                        if (!"null".equalsIgnoreCase(preco_custoString)) {
//                            preco_custo = new BigDecimal(preco_custoString);
//                            produtoRequest.setPreco_custo(preco_custo);
//                        }
//                    }
//
//                    NodeList peso_brutoNodes = doc.getElementsByTagName("peso_bruto");
//                    BigDecimal peso_bruto = null;
//                    if (peso_brutoNodes != null && peso_brutoNodes.getLength() > 0) {
//                        String peso_brutoString = peso_brutoNodes.item(0).getTextContent();
//                        if (!"null".equalsIgnoreCase(peso_brutoString)) {
//                            peso_bruto = new BigDecimal(peso_brutoString);
//                            produtoRequest.setPeso_bruto(peso_bruto);
//                        }
//                    }
//
//                    NodeList peso_liqNodes = doc.getElementsByTagName("peso_liq");
//                    BigDecimal peso_liq = null;
//                    if (peso_liqNodes != null && peso_liqNodes.getLength() > 0) {
//                        String peso_liqString = peso_liqNodes.item(0).getTextContent();
//                        if (!"null".equalsIgnoreCase(peso_liqString)) {
//                            peso_liq = new BigDecimal(peso_liqString);
//                            produtoRequest.setPeso_liq(peso_liq);
//                        }
//                    }
//
//                    NodeList class_fiscalNodes = doc.getElementsByTagName("class_fiscal");
//                    String class_fiscal = "";
//                    if (class_fiscalNodes != null && class_fiscalNodes.getLength() > 0) {
//                        class_fiscal = class_fiscalNodes.item(0).getTextContent();
//                        produtoRequest.setClass_fiscal(class_fiscal);
//                    }
//
//                    NodeList marcaNodes = doc.getElementsByTagName("marca");
//                    String marca = "";
//                    if (marcaNodes != null && marcaNodes.getLength() > 0) {
//                        marca = marcaNodes.item(0).getTextContent();
//                        produtoRequest.setMarca(marca);
//                    }
//
//                    NodeList cestNodes = doc.getElementsByTagName("cest");
//                    String cest = "";
//                    if (cestNodes != null && cestNodes.getLength() > 0) {
//                        cest = cestNodes.item(0).getTextContent();
//                        produtoRequest.setCest(cest);
//                    }
//
//                    NodeList origemNodes = doc.getElementsByTagName("origem");
//                    String origem = "";
//                    if (origemNodes != null && origemNodes.getLength() > 0) {
//                        origem = origemNodes.item(0).getTextContent();
//                        produtoRequest.setOrigem(origem);
//                    }
//
//                    NodeList idGrupoProdutoNodes = doc.getElementsByTagName("idGrupoProduto");
//                    BigDecimal idGrupoProduto = null;
//                    if (idGrupoProdutoNodes != null && idGrupoProdutoNodes.getLength() > 0) {
//                        String idGrupoProdutoString = idGrupoProdutoNodes.item(0).getTextContent();
//                        idGrupoProduto = new BigDecimal(idGrupoProdutoString);
//                        produtoRequest.setIdGrupoProduto(idGrupoProduto);
//                    }
//
//                    NodeList condicaoNodes = doc.getElementsByTagName("condicao");
//                    String condicao = "";
//                    if (condicaoNodes != null && condicaoNodes.getLength() > 0) {
//                        condicao = condicaoNodes.item(0).getTextContent();
//                        produtoRequest.setCondicao(condicao);
//                    }
//
//                    NodeList freteGratisNodes = doc.getElementsByTagName("freteGratis");
//                    String freteGratis = "";
//                    if (freteGratisNodes != null && freteGratisNodes.getLength() > 0) {
//                        freteGratis = freteGratisNodes.item(0).getTextContent();
//                        produtoRequest.setFreteGratis(freteGratis);
//                    }
//
//                    NodeList linkExternoNodes = doc.getElementsByTagName("linkExterno");
//                    String linkExterno = "";
//                    if (linkExternoNodes != null && linkExternoNodes.getLength() > 0) {
//                        linkExterno = linkExternoNodes.item(0).getTextContent();
//                        produtoRequest.setLinkExterno(linkExterno);
//                    }
//
//                    NodeList observacoesNodes = doc.getElementsByTagName("observacoes");
//                    String observacoes = "";
//                    if (observacoesNodes != null && observacoesNodes.getLength() > 0) {
//                        observacoes = observacoesNodes.item(0).getTextContent();
//                        produtoRequest.setObservacoes(observacoes);
//                    }
//
//                    NodeList producaoNodes = doc.getElementsByTagName("producao");
//                    String producao = "";
//                    if (producaoNodes != null && producaoNodes.getLength() > 0) {
//                        producao = producaoNodes.item(0).getTextContent();
//                        produtoRequest.setProducao(producao);
//                    }
//
//                    NodeList unidadeMedidaNodes = doc.getElementsByTagName("unidadeMedida");
//                    String unidadeMedida = "";
//                    if (unidadeMedidaNodes != null && unidadeMedidaNodes.getLength() > 0) {
//                        unidadeMedida = unidadeMedidaNodes.item(0).getTextContent();
//                        produtoRequest.setUnidadeMedida(unidadeMedida);
//                    }
//
//                    NodeList dataValidadeNodes = doc.getElementsByTagName("dataValidade");
//                    String dataValidade = "";
//                    if (dataValidadeNodes != null && dataValidadeNodes.getLength() > 0) {
//                        dataValidade = dataValidadeNodes.item(0).getTextContent();
//                        produtoRequest.setDataValidade(dataValidade);
//                    }
//
//                    NodeList descricaoFornecedorNodes = doc.getElementsByTagName("descricaoFornecedor");
//                    String descricaoFornecedor = "";
//                    if (descricaoFornecedorNodes != null && descricaoFornecedorNodes.getLength() > 0) {
//                        descricaoFornecedor = descricaoFornecedorNodes.item(0).getTextContent();
//                        produtoRequest.setDescricaoFornecedor(descricaoFornecedor);
//                    }
//
//                    NodeList idFabricanteNodes = doc.getElementsByTagName("idFabricante");
//                    BigDecimal idFabricante = null;
//                    if (idFabricanteNodes != null && idFabricanteNodes.getLength() > 0) {
//                        String idFabricanteString = idFabricanteNodes.item(0).getTextContent();
//                        if (!"null".equalsIgnoreCase(idFabricanteString)) {
//                            idFabricante = new BigDecimal(idFabricanteString);
//                            produtoRequest.setIdFabricante(idFabricante);
//                        }
//                    }
//
//                    NodeList codigoFabricanteNodes = doc.getElementsByTagName("codigoFabricante");
//                    String codigoFabricante = "";
//                    if (codigoFabricanteNodes != null && codigoFabricanteNodes.getLength() > 0) {
//                        codigoFabricante = codigoFabricanteNodes.item(0).getTextContent();
//                        produtoRequest.setCodigoFabricante(codigoFabricante);
//                    }
//
//                    NodeList gtinNodes = doc.getElementsByTagName("gtin");
//                    String gtin = "";
//                    if (gtinNodes != null && gtinNodes.getLength() > 0) {
//                        gtin = gtinNodes.item(0).getTextContent();
//                        produtoRequest.setGtin(gtin);
//                    }
//
//                    NodeList gtinEmbalagemNodes = doc.getElementsByTagName("gtinEmbalagem");
//                    String gtinEmbalagem = "";
//                    if (gtinEmbalagemNodes != null && gtinEmbalagemNodes.getLength() > 0) {
//                        gtinEmbalagem = gtinEmbalagemNodes.item(0).getTextContent();
//                        produtoRequest.setGtinEmbalagem(gtinEmbalagem);
//                    }
//
//                    NodeList larguraNodes = doc.getElementsByTagName("largura");
//                    String largura = "";
//                    if (larguraNodes != null && larguraNodes.getLength() > 0) {
//                        largura = larguraNodes.item(0).getTextContent();
//                        produtoRequest.setLargura(largura);
//                    }
//
//                    NodeList alturaNodes = doc.getElementsByTagName("altura");
//                    String altura = "";
//                    if (alturaNodes != null && alturaNodes.getLength() > 0) {
//                        altura = alturaNodes.item(0).getTextContent();
//                        produtoRequest.setAltura(altura);
//                    }
//
//                    NodeList profundidadeNodes = doc.getElementsByTagName("profundidade");
//                    String profundidade = "";
//                    if (profundidadeNodes != null && profundidadeNodes.getLength() > 0) {
//                        profundidade = profundidadeNodes.item(0).getTextContent();
//                        produtoRequest.setProfundidade(profundidade);
//                    }
//
//                    NodeList estoqueMinimoNodes = doc.getElementsByTagName("estoqueMinimo");
//                    BigDecimal estoqueMinimo = null;
//                    if (estoqueMinimoNodes != null && estoqueMinimoNodes.getLength() > 0) {
//                        String estoqueMinimoString = estoqueMinimoNodes.item(0).getTextContent();
//                        estoqueMinimo = new BigDecimal(estoqueMinimoString);
//                        produtoRequest.setEstoqueMinimo(estoqueMinimo);
//                    }
//
//                    NodeList estoqueMaximoNodes = doc.getElementsByTagName("estoqueMaximo");
//                    BigDecimal estoqueMaximo = null;
//                    if (estoqueMaximoNodes != null && estoqueMaximoNodes.getLength() > 0) {
//                        String estoqueMaximoString = estoqueMaximoNodes.item(0).getTextContent();
//                        estoqueMaximo = new BigDecimal(estoqueMaximoString);
//                        produtoRequest.setEstoqueMaximo(estoqueMaximo);
//                    }
//
//                    NodeList estoqueNodes = doc.getElementsByTagName("estoque");
//                    Integer estoque = null;
//                    if (estoqueNodes != null && estoqueNodes.getLength() > 0) {
//                        String estoqueString = estoqueNodes.item(0).getTextContent().trim();
//                        if (!"null".equalsIgnoreCase(estoqueString)) {
//                            estoque = Integer.valueOf(estoqueString);
//                            produtoRequest.setEstoque(estoque);
//                        }
//                    }
//
//                    NodeList itensPorCaixaNodes = doc.getElementsByTagName("itensPorCaixa");
//                    BigDecimal itensPorCaixa = null;
//                    if (itensPorCaixaNodes != null && itensPorCaixaNodes.getLength() > 0) {
//                        String estoqueMaximoString = itensPorCaixaNodes.item(0).getTextContent();
//                        itensPorCaixa = new BigDecimal(estoqueMaximoString);
//                        produtoRequest.setItensPorCaixa(itensPorCaixa);
//                    }
//
//                    NodeList volumesNodes = doc.getElementsByTagName("volumes");
//                    BigDecimal volumes = null;
//                    if (volumesNodes != null && volumesNodes.getLength() > 0) {
//                        String estoqueMaximoString = volumesNodes.item(0).getTextContent();
//                        volumes = new BigDecimal(estoqueMaximoString);
//                        produtoRequest.setVolumes(volumes);
//                    }
//
//                    NodeList urlVideoNodes = doc.getElementsByTagName("urlVideo");
//                    String urlVideo = "";
//                    if (urlVideoNodes != null && urlVideoNodes.getLength() > 0) {
//                        urlVideo = urlVideoNodes.item(0).getTextContent();
//                        produtoRequest.setUrlVideo(urlVideo);
//                    }
//
//                    NodeList localizacaoNodes = doc.getElementsByTagName("localizacao");
//                    String localizacao = "";
//                    if (localizacaoNodes != null && localizacaoNodes.getLength() > 0) {
//                        localizacao = localizacaoNodes.item(0).getTextContent();
//                        produtoRequest.setLocalizacao(localizacao);
//                    }
//
//                    NodeList crossdockingNodes = doc.getElementsByTagName("crossdocking");
//                    BigDecimal crossdocking = null;
//                    if (crossdockingNodes != null && crossdockingNodes.getLength() > 0) {
//                        String crossdockingString = crossdockingNodes.item(0).getTextContent();
//                        crossdocking = new BigDecimal(crossdockingString);
//                        produtoRequest.setCrossdocking(crossdocking);
//                    }
//
//                    NodeList garantiaNodes = doc.getElementsByTagName("garantia");
//                    Integer garantia = null;
//                    if (garantiaNodes != null && garantiaNodes.getLength() > 0) {
//                        String garantiaString = garantiaNodes.item(0).getTextContent().trim();
//                        if (!"null".equalsIgnoreCase(garantiaString)) {
//                            garantia = Integer.valueOf(garantiaString);
//                            produtoRequest.setEstoque(garantia);
//                        }
//                    }
//
//                    NodeList spedTipoItemNodes = doc.getElementsByTagName("spedTipoItem");
//                    Integer spedTipoItem = null;
//                    if (spedTipoItemNodes != null && spedTipoItemNodes.getLength() > 0) {
//                        String spedTipoItemString = spedTipoItemNodes.item(0).getTextContent().trim();
//                        if (!"null".equalsIgnoreCase(spedTipoItemString)) {
//                            spedTipoItem = Integer.valueOf(spedTipoItemString);
//                            produtoRequest.setEstoque(spedTipoItem);
//                        }
//                    }
//
//                    NodeList idCategoriaNodes = doc.getElementsByTagName("idCategoria");
//                    String idCategoria = "";
//                    if (idCategoriaNodes != null && idCategoriaNodes.getLength() > 0) {
//                        idCategoria = idCategoriaNodes.item(0).getTextContent();
//                        produtoRequest.setIdCategoria(idCategoria);
//                    }
//
//                    produtoRequest.setFlag("PUT");
//
//                    // Verifique se o produto já existe no banco de dados
//                    String descricaoProdutoExistente = doc.getElementsByTagName("descricao").item(0).getTextContent();
//                    List<ProdutoRequest> produtoExistente = produtoRequestRepository.findByDescricao(descricaoProdutoExistente);
//                    boolean produtoJaExiste = !produtoExistente.isEmpty();
//
//                    if (!produtoJaExiste) {
//                        produtoRequestRepository.save(produtoRequest);
//                    }
//
//                    //Atualiza na tabela tb_categoria_response a categoria atualizada para acesso imediato.
//                    ProdutoResponse produtoResponse = new ProdutoResponse();
//
//                    produtoResponse.setId(Long.valueOf(id));
//                    produtoResponse.setCodigo(codigoProduto);
//                    produtoResponse.setDescricao(descricao);
//                    produtoResponse.setTipo(tipo);
//                    produtoResponse.setSituacao(situacao);
//                    produtoResponse.setDescricaoCurta(descricaoCurta);
//                    produtoResponse.setDescricaoComplementar(descricaoComplementar);
//                    produtoResponse.setUnidadeMedida(un);
//                    produtoResponse.setPreco(String.valueOf(vlr_unit));
////                    produtoResponse.setvlr_desc(vlr_desc);
//                    produtoResponse.setPrecoCusto(String.valueOf(preco_custo));
//                    produtoResponse.setPesoBruto(String.valueOf(peso_bruto));
//                    produtoResponse.setPesoLiq(String.valueOf(peso_liq));
//                    produtoResponse.setClass_fiscal(class_fiscal);
//                    produtoResponse.setMarca(marca);
//                    produtoResponse.setCest(cest);
//                    produtoResponse.setOrigem(origem);
//                    produtoResponse.setIdGrupoProduto(String.valueOf(idGrupoProduto));
//                    produtoResponse.setCondicao(condicao);
//                    produtoResponse.setFreteGratis(freteGratis);
//                    produtoResponse.setLinkExterno(linkExterno);
//                    produtoResponse.setObservacoes(observacoes);
//                    produtoResponse.setProducao(producao);
//                    produtoResponse.setUnidadeMedida(unidadeMedida);
//                    produtoResponse.setDataValidade(dataValidade);
//                    produtoResponse.setDescricaoFornecedor(descricaoFornecedor);
//                    produtoResponse.setIdFabricante(String.valueOf(idFabricante));
//                    produtoResponse.setCodigoFabricante(codigoFabricante);
//                    produtoResponse.setGtin(gtin);
//                    produtoResponse.setGtinEmbalagem(gtinEmbalagem);
//                    produtoResponse.setLarguraProduto(largura);
//                    produtoResponse.setAlturaProduto(altura);
//                    produtoResponse.setProfundidadeProduto(profundidade);
//                    produtoResponse.setEstoqueMinimo(String.valueOf(estoqueMinimo));
//                    produtoResponse.setEstoqueMaximo(String.valueOf(estoqueMaximo));
////                    produtoResponse.setestoque(String.valueOf(estoque));
//                    produtoResponse.setItensPorCaixa(String.valueOf(itensPorCaixa));
//                    produtoResponse.setVolumes(String.valueOf(volumes));
//                    produtoResponse.setUrlVideo(urlVideo);
//                    produtoResponse.setLocalizacao(localizacao);
//                    produtoResponse.setCrossdocking(String.valueOf(crossdocking));
//                    produtoResponse.setGarantia(String.valueOf(garantia));
//                    produtoResponse.setSpedTipoItem(String.valueOf(spedTipoItem));
////                    produtoResponse.categoria.setId(Long.valueOf(idCategoria));
//
//                    produtoResponseRepository.save(produtoResponse);
//
//                    logger.info("Dados atualizados no banco local.");
}