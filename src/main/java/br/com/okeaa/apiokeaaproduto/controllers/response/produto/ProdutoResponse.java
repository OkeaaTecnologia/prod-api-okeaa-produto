package br.com.okeaa.apiokeaaproduto.controllers.response.produto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PRODUTO_RESPONSE")
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoResponse {

    @Id
    @JsonProperty("id")
    public Long id;

    @JsonProperty("codigo")
    public String codigo;

    @JsonProperty("descricao")
    public String descricao;

    @JsonProperty("tipo")
    public String tipo;

    @JsonProperty("situacao")
    public String situacao;

    @JsonProperty("unidade")
    public String unidade;

    @JsonProperty("preco")
    public String preco;

    @JsonProperty("precoCusto")
    public String precoCusto;

    @JsonProperty("descricaoCurta")
    public String descricaoCurta;

    @JsonProperty("descricaoComplementar")
    public String descricaoComplementar;

    @JsonProperty("dataInclusao")
    public String dataInclusao;

    @JsonProperty("dataAlteracao")
    public String dataAlteracao;

    @JsonProperty("imageThumbnail")
    public String imageThumbnail;

    @JsonProperty("urlVideo")
    public String urlVideo;

    @JsonProperty("nomeFornecedor")
    public String nomeFornecedor;

    @JsonProperty("codigoFabricante")
    public String codigoFabricante;

    @JsonProperty("marca")
    public String marca;

    @JsonProperty("class_fiscal")
    public String class_fiscal;

    @JsonProperty("cest")
    public String cest;

    @JsonProperty("origem")
    public String origem;

    @JsonProperty("idGrupoProduto")
    public String idGrupoProduto;

    @JsonProperty("linkExterno")
    public String linkExterno;

    @JsonProperty("observacoes")
    public String observacoes;

    @JsonProperty("grupoProduto")
    public String grupoProduto;

    @JsonProperty("garantia")
    public String garantia;

    @JsonProperty("descricaoFornecedor")
    public String descricaoFornecedor;

    @JsonProperty("idFabricante")
    public String idFabricante;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria_id")
    @JsonProperty("categoria")
    public ProdutoCategoriaResponse categoria;

    @JsonProperty("pesoLiq")
    public String pesoLiq;

    @JsonProperty("pesoBruto")
    public String pesoBruto;

    @JsonProperty("estoqueMinimo")
    public String estoqueMinimo;

    @JsonProperty("estoqueMaximo")
    public String estoqueMaximo;

    @JsonProperty("gtin")
    public String gtin;

    @JsonProperty("gtinEmbalagem")
    public String gtinEmbalagem;

    @JsonProperty("larguraProduto")
    public String larguraProduto;

    @JsonProperty("alturaProduto")
    public String alturaProduto;

    @JsonProperty("profundidadeProduto")
    public String profundidadeProduto;

    @JsonProperty("unidadeMedida")
    public String unidadeMedida;

    @JsonProperty("itensPorCaixa")
    public String itensPorCaixa;

    @JsonProperty("volumes")
    public String volumes;

    @JsonProperty("localizacao")
    public String localizacao;

    @JsonProperty("crossdocking")
    public String crossdocking;

    @JsonProperty("condicao")
    public String condicao;

    @JsonProperty("freteGratis")
    public String freteGratis;

    @JsonProperty("producao")
    public String producao;

    @JsonProperty("dataValidade")
    public String dataValidade;

    @JsonProperty("spedTipoItem")
    public String spedTipoItem;

    // Propriedade adicional "Valor do desconto".
    @JsonProperty("precoDescontoLista")
    public BigDecimal precoDescontoLista;

    // Propriedade adicional "Nome da lista de desconto".
    @JsonProperty("listaDesconto")
    public String listaDesconto;

    @Override
    public String toString() {
        return "ProdutoResponse{" +
                "id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                ", situacao='" + situacao + '\'' +
                ", unidade='" + unidade + '\'' +
                ", preco='" + preco + '\'' +
                ", precoCusto='" + precoCusto + '\'' +
                ", descricaoCurta='" + descricaoCurta + '\'' +
                ", descricaoComplementar='" + descricaoComplementar + '\'' +
                ", dataInclusao='" + dataInclusao + '\'' +
                ", dataAlteracao='" + dataAlteracao + '\'' +
                ", imageThumbnail='" + imageThumbnail + '\'' +
                ", urlVideo='" + urlVideo + '\'' +
                ", nomeFornecedor='" + nomeFornecedor + '\'' +
                ", codigoFabricante='" + codigoFabricante + '\'' +
                ", marca='" + marca + '\'' +
                ", class_fiscal='" + class_fiscal + '\'' +
                ", cest='" + cest + '\'' +
                ", origem='" + origem + '\'' +
                ", idGrupoProduto='" + idGrupoProduto + '\'' +
                ", linkExterno='" + linkExterno + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", grupoProduto='" + grupoProduto + '\'' +
                ", garantia='" + garantia + '\'' +
                ", descricaoFornecedor='" + descricaoFornecedor + '\'' +
                ", idFabricante='" + idFabricante + '\'' +
                ", categoria=" + (categoria != null ? categoria.getId() : null) +
                ", pesoLiq='" + pesoLiq + '\'' +
                ", pesoBruto='" + pesoBruto + '\'' +
                ", estoqueMinimo='" + estoqueMinimo + '\'' +
                ", estoqueMaximo='" + estoqueMaximo + '\'' +
                ", gtin='" + gtin + '\'' +
                ", gtinEmbalagem='" + gtinEmbalagem + '\'' +
                ", larguraProduto='" + larguraProduto + '\'' +
                ", alturaProduto='" + alturaProduto + '\'' +
                ", profundidadeProduto='" + profundidadeProduto + '\'' +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", itensPorCaixa=" + itensPorCaixa +
                ", volumes=" + volumes +
                ", localizacao='" + localizacao + '\'' +
                ", crossdocking='" + crossdocking + '\'' +
                ", condicao='" + condicao + '\'' +
                ", freteGratis='" + freteGratis + '\'' +
                ", producao='" + producao + '\'' +
                ", dataValidade='" + dataValidade + '\'' +
                ", spedTipoItem='" + spedTipoItem + '\'' +
                ", precoDescontoLista=" + precoDescontoLista +
                ", listaDesconto='" + listaDesconto + '\'' +
                '}';
    }
}