package br.com.gagjunior.bootcampxpedu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "produtos")
public record Produtos(
        @Id
        String id,
        String codigo,
        String nome,
        String descricao,
        Double preco
) {
    public Produtos {
        validarCamposTexto("Codigo", codigo);
        validarCamposTexto("Nome", nome);
        validarCamposTexto("Descricao", descricao);
        validarCamposNumericos(preco);

    }

    private void validarCamposNumericos(Double valor) {
        Objects.requireNonNull(valor, "Preco" + " não pode ser nulo");
        if (valor <= 0) {
            throw new IllegalArgumentException("Preco" + " não pode ser zero ou negativo");
        }
    }

    private void validarCamposTexto(String campo, String valor) {
        Objects.requireNonNull(valor, campo + " não pode ser nulo");
        if (valor.trim().isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
    }
}
