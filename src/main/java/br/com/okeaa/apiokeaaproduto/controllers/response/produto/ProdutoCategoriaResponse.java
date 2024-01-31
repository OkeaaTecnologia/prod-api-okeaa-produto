package br.com.okeaa.apiokeaaproduto.controllers.response.produto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_PRODUTO_CATEGORIA_RESPONSE")
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoCategoriaResponse {

    @Id
    @JsonProperty("id")
    public Long id;

    @JsonProperty("descricao")
    public String descricao;

    @JsonProperty("idCategoriaPai")
    public String idCategoriaPai;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<ProdutoResponse> produtos = new ArrayList<>();

    @Override
    public String toString() {
        return "ProdutoCategoriaResponse{" +
                "id=" + id + '\'' +
                ", descricao='" + descricao + '\'' +
                ", idCategoriaPai='" + idCategoriaPai + '\'' +
                '}';
    }
}

