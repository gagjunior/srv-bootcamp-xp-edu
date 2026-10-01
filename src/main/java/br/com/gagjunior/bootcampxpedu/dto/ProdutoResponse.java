package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Produtos;
import io.swagger.v3.oas.annotations.media.Schema;

/** Representação pública de um produto. */
@Schema(name = "ProdutoResponse", description = "Representação pública de um produto.")
public record ProdutoResponse(
        @Schema(description = "Identificador único do produto", example = "65f1a2b3c4d5e6f789012346")
        String id,
        @Schema(description = "Código único do produto", example = "PROD-001")
        String codigo,
        @Schema(description = "Nome do produto", example = "Teclado mecânico")
        String nome,
        @Schema(description = "Descrição detalhada do produto", example = "Teclado mecânico ABNT2 com iluminação RGB")
        String descricao,
        @Schema(description = "Preço unitário do produto", example = "249.90", minimum = "0.01")
        Double preco
) {

    public static ProdutoResponse from(Produtos produto) {
        return new ProdutoResponse(
                produto.id(), produto.codigo(), produto.nome(), produto.descricao(), produto.preco());
    }
}
