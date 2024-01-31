package br.com.okeaa.apiokeaaproduto.controllers.response.listaPreco;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PRODUTO_LISTA_RESPONSE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoLista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("idBd")
    public Long idBd;

    @JsonProperty("id")
    public Long id;

    @JsonProperty("codigo")
    public String codigo;

    @JsonProperty("descricao")
    public String descricao;

    @JsonProperty("preco")
    public String preco;

    @JsonProperty("precoLista")
    public String precoLista;

    @JsonProperty("gtin")
    public String gtin;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "listaPreco_id", referencedColumnName = "id")
    public ListaPrecoResponse listaPreco;

    @Override
    public String toString() {
        return "ProdutoLista{" +
                "idBd='" + idBd + '\'' +
                ", id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco='" + preco + '\'' +
                ", precoLista='" + precoLista + '\'' +
                ", gtin='" + gtin + '\'' +
                ", listaPreco=" + (listaPreco != null ? listaPreco.getId() : null) +
                '}';
    }
}

