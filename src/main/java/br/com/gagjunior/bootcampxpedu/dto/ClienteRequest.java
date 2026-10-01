package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Clientes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/** Dados aceitos para criação e alteração de clientes. */
@Schema(name = "ClienteRequest", description = "Dados para criação ou alteração de um cliente.")
public record ClienteRequest(
        @Schema(description = "Nome completo do cliente", example = "Ana Silva", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @Schema(description = "CPF do cliente", example = "12345678900", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "cpf é obrigatório")
        String cpf,

        @Schema(description = "E-mail do cliente", example = "ana.silva@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email deve possuir um formato válido")
        String email
) {

    public Clientes toModel() {
        return new Clientes(null, nome.trim(), cpf.trim(), email.trim());
    }
}
