package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Clientes;
import io.swagger.v3.oas.annotations.media.Schema;

/** Representação pública de um cliente. */
@Schema(name = "ClienteResponse", description = "Representação pública de um cliente.")
public record ClienteResponse(
        @Schema(description = "Identificador único do cliente", example = "65f1a2b3c4d5e6f789012345")
        String id,
        @Schema(description = "Nome completo do cliente", example = "Ana Silva")
        String nome,
        @Schema(description = "CPF do cliente", example = "12345678900")
        String cpf,
        @Schema(description = "E-mail do cliente", example = "ana.silva@example.com")
        String email
) {

    public static ClienteResponse from(Clientes cliente) {
        return new ClienteResponse(cliente.id(), cliente.nome(), cliente.cpf(), cliente.email());
    }
}
