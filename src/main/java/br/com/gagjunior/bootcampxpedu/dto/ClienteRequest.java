package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Clientes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Dados aceitos para criação e alteração de clientes. */
public record ClienteRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotBlank(message = "cpf é obrigatório")
        String cpf,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email deve possuir um formato válido")
        String email
) {

    public Clientes toModel() {
        return new Clientes(null, nome.trim(), cpf.trim(), email.trim());
    }
}
