package br.com.gagjunior.bootcampxpedu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "clientes")
public record Clientes(
        @Id
        String id,
        String nome,
        String cpf,
        String email
) {
    public Clientes {
        validarCamposObrigatorios("Nome", nome);
        validarCamposObrigatorios("CPF", cpf);
        validarCamposObrigatorios("Email", email);
    }

    private void validarCamposObrigatorios(String campo, String valor) {
        Objects.requireNonNull(valor, campo + " não pode ser nulo");
        if (valor.trim().isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
    }
}
