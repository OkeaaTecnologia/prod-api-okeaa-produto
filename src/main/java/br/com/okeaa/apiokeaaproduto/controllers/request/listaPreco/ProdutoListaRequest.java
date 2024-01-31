package br.com.okeaa.apiokeaaproduto.controllers.request.listaPreco;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PRODUTO_LISTA_REQUEST")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoListaRequest {

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

    @JsonProperty("precoCusto")
    public String precoCusto;

    @JsonProperty("imageThumbnail")
    public String imageThumbnail;

    @JsonProperty("gtin")
    public String gtin;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "listaPreco_id", referencedColumnName = "id")
    public ListaPrecoRequest listaPreco;

//    @Override
//    public String toString() {
//        return "ProdutoLista{" +
//                "idBd='" + idBd + '\'' +
//                ", id='" + id + '\'' +
//                ", codigo='" + codigo + '\'' +
//                ", descricao='" + descricao + '\'' +
//                ", preco='" + preco + '\'' +
//                ", precoCusto='" + precoCusto + '\'' +
//                ", imageThumbnail='" + imageThumbnail + '\'' +
//                ", gtin='" + gtin + '\'' +
//                ", listaPreco=" + (listaPreco != null ? listaPreco.getId() : null) +
//                '}';
//    }
}

