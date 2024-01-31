package br.com.okeaa.apiokeaaproduto.controllers.request.listaPreco;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_LISTA_PRECO_REQUEST")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListaPrecoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    public Long id;

    @JsonProperty("idLista")
    public String idLista;

    @JsonProperty("nomeLista")
    public String nomeLista;

    @JsonProperty("quantidadeProduto")
    public String quantidadeProduto;

    @JsonProperty("regraLista")
    public String regraLista;

    @JsonProperty("dataValidade")
    public String dataValidade;

    @JsonProperty("horarioVigencia")
    public String horarioVigencia;

    @JsonProperty("tipoLista")
    public String tipoLista;

    @JsonProperty("baseado")
    public String baseado;

    @JsonProperty("fatorAplicado")
    public String fatorAplicado;

    @JsonManagedReference
    @OneToMany(mappedBy = "listaPreco", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ProdutoListaRequest> produtoLista = new ArrayList<>();

    @Override
    public String toString() {
        return "ListaPrecoResponse{" +
                "id='" + id + '\'' +
                ", idLista='" + idLista + '\'' +
                ", nomeLista='" + nomeLista + '\'' +
                ", quantidadeProduto='" + quantidadeProduto + '\'' +
                ", regraLista='" + regraLista + '\'' +
                ", dataValidade='" + dataValidade + '\'' +
                ", horarioVigencia='" + horarioVigencia + '\'' +
                ", produtoLista=" + produtoLista +
                '}';
    }

}