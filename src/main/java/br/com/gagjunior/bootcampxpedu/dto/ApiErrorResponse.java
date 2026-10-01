package br.com.gagjunior.bootcampxpedu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/** Contrato único de erro retornado pela API. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "ApiErrorResponse", description = "Contrato padronizado de erro da API.")
public record ApiErrorResponse(
        @Schema(description = "Data e hora do erro em UTC", example = "2026-10-01T23:15:30Z")
        Instant timestamp,
        @Schema(description = "Código HTTP retornado", example = "400")
        int status,
        @Schema(description = "Descrição do status HTTP", example = "Bad Request")
        String error,
        @Schema(description = "Mensagem explicativa do erro", example = "A requisição possui campos inválidos")
        String message,
        @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/v1/clientes")
        String path,
        @ArraySchema(schema = @Schema(implementation = ApiFieldError.class, description = "Erro de validação associado a um campo"))
        List<ApiFieldError> errors
) {
}
