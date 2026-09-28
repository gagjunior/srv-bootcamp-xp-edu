package br.com.gagjunior.bootcampxpedu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pedidos")
public record Pedidos(
        @Id
        String id,
        String clienteId,
        List<String> produtosId,
        Double valorTotal,
        String status
) {
}
