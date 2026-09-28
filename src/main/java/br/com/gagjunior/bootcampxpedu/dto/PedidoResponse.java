package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;

import java.util.List;

/** Representação pública de um pedido. */
public record PedidoResponse(
        String id,
        String clienteId,
        List<String> produtosId,
        Double valorTotal,
        String status
) {

    public static PedidoResponse from(Pedidos pedido) {
        return new PedidoResponse(
                pedido.id(), pedido.clienteId(), List.copyOf(pedido.produtosId()), pedido.valorTotal(), pedido.status());
    }
}
