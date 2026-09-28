package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Produtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Dados aceitos para criação e alteração de produtos. */
public record ProdutoRequest(
        @NotBlank(message = "codigo é obrigatório")
        String codigo,

        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotBlank(message = "descricao é obrigatória")
        String descricao,

        @NotNull(message = "preco é obrigatório")
        @Positive(message = "preco deve ser maior que zero")
        Double preco
) {

    public Produtos toModel() {
        return new Produtos(null, codigo.trim(), nome.trim(), descricao.trim(), preco);
    }
}
