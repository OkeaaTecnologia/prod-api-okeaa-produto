package br.com.okeaa.apiokeaaproduto.controllers.request.produto;

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
@Table(name = "TB_PRODUTO_IMAGEM_REQUEST")
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImagemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonProperty("url")
    public String url;

    @OneToMany(mappedBy = "imagens", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<ProdutoRequest> produtos = new ArrayList<>();

    @Override
    public String toString() {
        return "ImagemRequest{" +
                "url='" + url + ";" +
                '}';
    }
}
