package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Dados aceitos para criação e alteração de pedidos. */
@Schema(name = "PedidoRequest", description = "Dados para criação ou alteração de um pedido.")
public record PedidoRequest(
        @Schema(description = "Identificador do cliente associado ao pedido", example = "65f1a2b3c4d5e6f789012345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "clienteId é obrigatório")
        String clienteId,

        @Schema(description = "Identificadores dos produtos do pedido", example = "[\"65f1a2b3c4d5e6f789012346\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "produtosId deve possuir pelo menos um produto")
        List<@NotBlank(message = "produtoId não pode ser vazio") String> produtosId,

        @Schema(description = "Valor total do pedido", example = "149.90", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "valorTotal é obrigatório")
        @PositiveOrZero(message = "valorTotal não pode ser negativo")
        Double valorTotal,

        @Schema(description = "Status atual do pedido", example = "RECEBIDO", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "status é obrigatório")
        String status
) {

    public Pedidos toModel() {
        return new Pedidos(null, clienteId.trim(), List.copyOf(produtosId), valorTotal, status.trim());
    }
}
