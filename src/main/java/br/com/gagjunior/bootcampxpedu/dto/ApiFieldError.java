package br.com.gagjunior.bootcampxpedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Erro de validação associado a um campo da requisição. */
@Schema(name = "ApiFieldError", description = "Erro de validação associado a um campo da requisição.")
public record ApiFieldError(
        @Schema(description = "Nome do campo inválido", example = "email")
        String field,
        @Schema(description = "Detalhe da validação", example = "email deve possuir um formato válido")
        String message
) {
}
