package br.com.gagjunior.bootcampxpedu.controller;

import br.com.gagjunior.bootcampxpedu.dto.ApiErrorResponse;
import br.com.gagjunior.bootcampxpedu.dto.PedidoRequest;
import br.com.gagjunior.bootcampxpedu.dto.PedidoResponse;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.service.PedidosService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Operações de cadastro e consulta de pedidos.")
public class PedidosController {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @GetMapping
    @Operation(summary = "Lista pedidos", description = "Lista todos os pedidos ou consulta por cliente ou status. Apenas um filtro pode ser informado por vez.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Filtros inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<PedidoResponse>> findAll(
            @Parameter(description = "Identificador do cliente para filtrar pedidos", example = "65f1a2b3c4d5e6f789012345")
            @RequestParam(value = "clienteId", required = false) String clienteId,
            @Parameter(description = "Status para filtrar pedidos", example = "RECEBIDO")
            @RequestParam(value = "status", required = false) String status
    ) {
        if (clienteId != null && status != null) {
            throw new IllegalArgumentException("informe apenas um dos filtros: clienteId ou status");
        }

        if (clienteId != null) {
            return ResponseEntity.ok(pedidosService.findByClienteId(clienteId).stream()
                    .map(PedidoResponse::from)
                    .toList());
        }
        if (status != null) {
            return ResponseEntity.ok(pedidosService.findByStatus(status).stream()
                    .map(PedidoResponse::from)
                    .toList());
        }

        return ResponseEntity.ok(pedidosService.findAll().stream().map(PedidoResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PedidoResponse> findById(
            @Parameter(description = "Identificador do pedido", example = "65f1a2b3c4d5e6f789012347", required = true)
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.findById(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Lista pedidos de um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "ID do cliente inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<PedidoResponse>> findByClienteId(
            @Parameter(description = "Identificador do cliente", example = "65f1a2b3c4d5e6f789012345", required = true)
            @PathVariable("clienteId") String clienteId
    ) {
        return ResponseEntity.ok(pedidosService.findByClienteId(clienteId).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lista pedidos por status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<PedidoResponse>> findByStatus(
            @Parameter(description = "Status do pedido", example = "RECEBIDO", required = true)
            @PathVariable("status") String status
    ) {
        return ResponseEntity.ok(pedidosService.findByStatus(status).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Cria um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto referenciado não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PedidoResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do pedido", required = true)
            @Valid @RequestBody PedidoRequest request
    ) {
        Pedidos saved = pedidosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(PedidoResponse.from(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido, cliente ou produto não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PedidoResponse> update(
            @Parameter(description = "Identificador do pedido", example = "65f1a2b3c4d5e6f789012347", required = true)
            @PathVariable("id") String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do pedido", required = true)
            @Valid @RequestBody PedidoRequest request
    ) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.update(id, request.toModel())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido excluído"),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do pedido", example = "65f1a2b3c4d5e6f789012347", required = true)
            @PathVariable("id") String id
    ) {
        pedidosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
