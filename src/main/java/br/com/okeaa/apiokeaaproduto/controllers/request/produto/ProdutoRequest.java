package br.com.okeaa.apiokeaaproduto.controllers.request.produto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PRODUTO_REQUEST")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long idBd;

    @JsonProperty("id")
    public Long id;

    //    @Size(max = 60, message = "Código do produto")
    @JsonProperty("codigo")
    public String codigo;

    //    @Size(max = 4, message = "Código de item de serviço (06, 21 e 22)")
    @JsonProperty("codigoItem")
    public Integer codigoItem;

    //    @NotBlank
//    @Size(max = 120, message = "Descrição do produto")
    @JsonProperty("descricao")
    public String descricao;

    //    @Size(max = 1, message = "Tipo ('S' para serviço, 'P' para produto e 'N' para serviço de nota 06, 21 e 22)")
    @JsonProperty("tipo")
    public String tipo;

    //    @Size(message = "Situação do produto (Ativo ou Inativo)")
    @JsonProperty("situacao")
    public String situacao;

    //    @Size(message = "Descrição curta do produto")
    @JsonProperty("descricaoCurta")
    public String descricaoCurta;

    //    @Size(message = "Descrição complementar do produto")
    @JsonProperty("descricaoComplementar")
    public String descricaoComplementar;

    //    @Size(max = 6, message = "Tipo de unidade do produto")
    @JsonProperty("un")
    public String un;

    //    @DecimalMin("0")
//    @Digits(integer = 17, fraction = 10)
//    @Size(message = "Valor unitário do produto")
    @JsonProperty("vlr_unit")
    public BigDecimal vlr_unit;

    //    @DecimalMin("0")
//    @Digits(integer = 17, fraction = 10)
//    @Size(message = "Valor de desconto do produto")
    @JsonProperty("vlr_desc")
    public BigDecimal vlr_desc;

    //    @Digits(integer = 17, fraction = 10)
//    @Size(message = "Preço de custo do produto")
    @JsonProperty("preco_custo")
    public BigDecimal preco_custo;

    //    @Digits(integer = 11, fraction = 3)
//    @Size(message = "Peso bruto do produto")
    @JsonProperty("peso_bruto")
    public BigDecimal peso_bruto;

    //    @Digits(integer = 11, fraction = 3)
//    @Size(message = "Peso líquido do produto")
    @JsonProperty("peso_liq")
    public BigDecimal peso_liq;

    //    @Size(max = 10, message = "NCM do produto")
    @JsonProperty("class_fiscal")
    public String class_fiscal;

    //    @Size(max = 40, message = "Marca do produto")
    @JsonProperty("marca")
    public String marca;

    //    @Size(max = 7, message = "CEST do produto")
    @JsonProperty("cest")
    public String cest;

    //    @Size(max = 1, message = "Origem do produto")
    @JsonProperty("origem")
    public String origem;

    //    @Size(max = 11, message = "Identificador do grupo do produto")
    @JsonProperty("idGrupoProduto")
    public BigDecimal idGrupoProduto;

    //    @Size(message = "Condição do produto (Não especificado, Novo, Recondicionado ou Usado)")
    @JsonProperty("condicao")
    public String condicao = "Não especificado";

    //    @Size(max = 1, message = "Frete grátis (S para Sim ou N para Não)")
    @JsonProperty("freteGratis")
    public String freteGratis;

    //    @Size(max = 100, message = "Link do produto na loja virtual, marketplace, catálago etc.")
    @JsonProperty("linkExterno")
    public String linkExterno;

    //    @Size(message = "Observações do produto")
    @JsonProperty("observacoes")
    public String observacoes;

    //    @Size(max = 1, message = "Tipo de produção do produto (T para Terceiros ou P para Própria)")
    @JsonProperty("producao")
    public String producao;

    //    @Size(message = "Unidade de medida do produto (Metros, Centímetros ou Milímetro)")
    @JsonProperty("unidadeMedida")
    public String unidadeMedida;

    //    @Size(max = 1, message = "Data de validade do produto")
//    @DateTimeFormat(pattern = "dd/mm/YYYY")
    @JsonProperty("dataValidade")
    public String dataValidade;

    //    @Size(message = "Descrição do fornecedor")
    @JsonProperty("descricaoFornecedor")
    public String descricaoFornecedor;

    //    @Size(max = 11, message = "Id do fornecedor (pode ser obtido no GET de contatos)")
    @JsonProperty("idFabricante")
    public BigDecimal idFabricante;

    //    @Size(message = "Código do produto no fornecedor")
    @JsonProperty("codigoFabricante")
    public String codigoFabricante;

    //    @Size(max = 14, message = "GTIN / EAN do produto")
    @JsonProperty("gtin")
    public String gtin;

    //    @Size(max = 14, message = "GTIN / EAN tributário da menor unidade comercializada")
    @JsonProperty("gtinEmbalagem")
    public String gtinEmbalagem;

    //    @Size(max = 15, message = "Largura do produto com embalagem")
    @JsonProperty("largura")
    public String largura;

    //    @Size(max = 15, message = "Altura do produto com embalagem")
    @JsonProperty("altura")
    public String altura;

    //    @Size(max = 15, message = "Profundidade do produto com embalagem")
    @JsonProperty("profundidade")
    public String profundidade;

    //    @DecimalMax(value = "9999999999.99", message = "Estoque mínimo do produto")
    @JsonProperty("estoqueMinimo")
    public BigDecimal estoqueMinimo;

    //    @DecimalMax(value = "9999999999.99", message = "Estoque máximo do produto deve ser menor ou igual a 9999999999.99")
    @JsonProperty("estoqueMaximo")
    public BigDecimal estoqueMaximo;

    @JsonProperty("estoque")
    public Integer estoque;

    //    @Digits(integer = 11, fraction = 2)
//    @Size(message = "Quantidade de itens por caixa")
    @JsonProperty("itensPorCaixa")
    public BigDecimal itensPorCaixa;

    //    @DecimalMax(value = "2", message = "Quantidade de volumes do produto deve ser menor ou igual a 2")
    @JsonProperty("volumes")
    public BigDecimal volumes;

    //    @Size(max = 100, message = "Url do vídeo do produto, utilizado na exportação do produto para lojas virtuais")
    @JsonProperty("urlVideo")
    public String urlVideo;

    //    @Size(max = 50, message = "Localização do produto")
    @JsonProperty("localizacao")
    public String localizacao;

    //    @Size(max = 4, message = "Quantidade de dias para o processo de distribuição em que a mercadoria recebida é redirecionada ao consumidor final sem uma armazenagem prévia.")
    @JsonProperty("crossdocking")
    public BigDecimal crossdocking;

    //    @Size(max = 3, message = "Garantia do produto, deve ser informada em meses.")
    @JsonProperty("garantia")
    public Integer garantia;

    //    @Size(max = 2, message = "Código do tipo do item no SPED")
    @JsonProperty("spedTipoItem")
    public Integer spedTipoItem;

    //    @Size(max = 11, message = "Quantidade de itens por caixa")
    @JsonProperty("idCategoria")
    public String idCategoria;

    @JsonIgnore
    public String flag;

    @ManyToOne
    @JoinColumn(name = "imagens_id") // Coluna de chave estrangeira referenciando a categoria
    @JsonProperty("imagens")
    public ImagemRequest imagens;

//    @JsonProperty("deposito")
//    public DepositoRequest deposito;
//
//    @JsonProperty("variacoes")
//    public VariacoesRequest variacoes;
//
//    @JsonProperty("camposCustomizados")
//    public CamposCustomizadosRequest camposCustomizados;

//    @JsonProperty("estrutura")
//    public EstruturaRequest estrutura;

    @Override
    public String toString() {
        return "ProdutoRequest{" +
                ", codigo='" + codigo + '\'' +
                ", codigoItem=" + codigoItem +
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                ", situacao='" + situacao + '\'' +
                ", descricaoCurta='" + descricaoCurta + '\'' +
                ", descricaoComplementar='" + descricaoComplementar + '\'' +
                ", un='" + un + '\'' +
                ", vlr_unit=" + vlr_unit +
                ", vlr_desc=" + vlr_desc +
                ", preco_custo=" + preco_custo +
                ", peso_bruto=" + peso_bruto +
                ", peso_liq=" + peso_liq +
                ", class_fiscal='" + class_fiscal + '\'' +
                ", marca='" + marca + '\'' +
                ", cest='" + cest + '\'' +
                ", origem='" + origem + '\'' +
                ", idGrupoProduto=" + idGrupoProduto +
                ", condicao='" + condicao + '\'' +
                ", freteGratis='" + freteGratis + '\'' +
                ", linkExterno='" + linkExterno + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", producao='" + producao + '\'' +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", dataValidade='" + dataValidade + '\'' +
                ", descricaoFornecedor='" + descricaoFornecedor + '\'' +
                ", idFabricante=" + idFabricante +
                ", codigoFabricante='" + codigoFabricante + '\'' +
                ", gtin='" + gtin + '\'' +
                ", gtinEmbalagem='" + gtinEmbalagem + '\'' +
                ", largura='" + largura + '\'' +
                ", altura='" + altura + '\'' +
                ", profundidade='" + profundidade + '\'' +
                ", estoqueMinimo=" + estoqueMinimo +
                ", estoqueMaximo=" + estoqueMaximo +
                ", estoque=" + estoque +
                ", itensPorCaixa=" + itensPorCaixa +
                ", volumes=" + volumes +
                ", urlVideo='" + urlVideo + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", crossdocking=" + crossdocking +
                ", garantia=" + garantia +
                ", spedTipoItem=" + spedTipoItem +
                ", idCategoria='" + idCategoria + '\'' +
                ", flag='" + flag + '\'' +
                ", imagens=" + (imagens != null ? imagens.getId() : null) + // Verifique se o cliente não é nulo antes de acessar seu id
                '}';
    }
}