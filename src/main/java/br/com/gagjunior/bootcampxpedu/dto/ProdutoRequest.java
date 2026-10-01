package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Produtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

/** Dados aceitos para criação e alteração de produtos. */
@Schema(name = "ProdutoRequest", description = "Dados para criação ou alteração de um produto.")
public record ProdutoRequest(
        @Schema(description = "Código único do produto", example = "PROD-001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "codigo é obrigatório")
        String codigo,

        @Schema(description = "Nome do produto", example = "Teclado mecânico", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @Schema(description = "Descrição detalhada do produto", example = "Teclado mecânico ABNT2 com iluminação RGB", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "descricao é obrigatória")
        String descricao,

        @Schema(description = "Preço unitário do produto", example = "249.90", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "preco é obrigatório")
        @Positive(message = "preco deve ser maior que zero")
        Double preco
) {

    public Produtos toModel() {
        return new Produtos(null, codigo.trim(), nome.trim(), descricao.trim(), preco);
    }
}
