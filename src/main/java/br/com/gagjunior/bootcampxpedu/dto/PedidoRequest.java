package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/** Dados aceitos para criação e alteração de pedidos. */
public record PedidoRequest(
        @NotBlank(message = "clienteId é obrigatório")
        String clienteId,

        @NotEmpty(message = "produtosId deve possuir pelo menos um produto")
        List<@NotBlank(message = "produtoId não pode ser vazio") String> produtosId,

        @NotNull(message = "valorTotal é obrigatório")
        @PositiveOrZero(message = "valorTotal não pode ser negativo")
        Double valorTotal,

        @NotBlank(message = "status é obrigatório")
        String status
) {

    public Pedidos toModel() {
        return new Pedidos(null, clienteId.trim(), List.copyOf(produtosId), valorTotal, status.trim());
    }
}
