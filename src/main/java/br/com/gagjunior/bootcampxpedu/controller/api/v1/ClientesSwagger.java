package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.ApiErrorResponse;
import br.com.gagjunior.bootcampxpedu.dto.ClienteRequest;
import br.com.gagjunior.bootcampxpedu.dto.ClienteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Operações de cadastro e consulta de clientes.")
public interface ClientesSwagger {

    @GetMapping
    @Operation(summary = "Lista clientes", description = "Lista todos os clientes ou consulta por CPF ou e-mail. Apenas um filtro pode ser informado por vez.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Filtros inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o filtro informado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<List<ClienteResponse>> findAll(
            @Parameter(description = "CPF para consulta exata", example = "12345678900")
            @RequestParam(value = "cpf", required = false) String cpf,
            @Parameter(description = "E-mail para consulta exata", example = "ana.silva@example.com")
            @RequestParam(value = "email", required = false) String email
    );

    @GetMapping("/{id}")
    @Operation(summary = "Busca cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<ClienteResponse> findById(
            @Parameter(description = "Identificador do cliente", example = "65f1a2b3c4d5e6f789012345", required = true)
            @PathVariable("id") String id
    );

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Busca cliente por CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "CPF inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<ClienteResponse> findByCpf(
            @Parameter(description = "CPF do cliente", example = "12345678900", required = true)
            @PathVariable("cpf") String cpf
    );

    @GetMapping("/email/{email}")
    @Operation(summary = "Busca cliente por e-mail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "E-mail inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<ClienteResponse> findByEmail(
            @Parameter(description = "E-mail do cliente", example = "ana.silva@example.com", required = true)
            @PathVariable("email") String email
    );

    @PostMapping
    @Operation(summary = "Cria um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado", content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<ClienteResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do cliente", required = true)
            @Valid @RequestBody ClienteRequest request
    );

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado", content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<ClienteResponse> update(
            @Parameter(description = "Identificador do cliente", example = "65f1a2b3c4d5e6f789012345", required = true)
            @PathVariable("id") String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do cliente", required = true)
            @Valid @RequestBody ClienteRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído"),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do cliente", example = "65f1a2b3c4d5e6f789012345", required = true)
            @PathVariable("id") String id
    );
}
