package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Representação pública de um pedido. */
@Schema(name = "PedidoResponse", description = "Representação pública de um pedido.")
public record PedidoResponse(
        @Schema(description = "Identificador único do pedido", example = "65f1a2b3c4d5e6f789012347")
        String id,
        @Schema(description = "Identificador do cliente associado", example = "65f1a2b3c4d5e6f789012345")
        String clienteId,
        @Schema(description = "Identificadores dos produtos do pedido", example = "[\"65f1a2b3c4d5e6f789012346\"]")
        List<String> produtosId,
        @Schema(description = "Valor total do pedido", example = "149.90", minimum = "0")
        Double valorTotal,
        @Schema(description = "Status atual do pedido", example = "RECEBIDO")
        String status
) {

    public static PedidoResponse from(Pedidos pedido) {
        return new PedidoResponse(
                pedido.id(), pedido.clienteId(), List.copyOf(pedido.produtosId()), pedido.valorTotal(), pedido.status());
    }
}
