package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Clientes;

/** Representação pública de um cliente. */
public record ClienteResponse(
        String id,
        String nome,
        String cpf,
        String email
) {

    public static ClienteResponse from(Clientes cliente) {
        return new ClienteResponse(cliente.id(), cliente.nome(), cliente.cpf(), cliente.email());
    }
}
