package br.com.gagjunior.bootcampxpedu.controller;

import br.com.gagjunior.bootcampxpedu.dto.ApiErrorResponse;
import br.com.gagjunior.bootcampxpedu.dto.ProdutoRequest;
import br.com.gagjunior.bootcampxpedu.dto.ProdutoResponse;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.service.ProdutosService;
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
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Operações de cadastro e consulta de produtos.")
public class ProdutosController {

    private final ProdutosService produtosService;

    public ProdutosController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @GetMapping
    @Operation(summary = "Lista produtos", description = "Lista todos os produtos ou consulta um produto pelo código.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProdutoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Código inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para o filtro informado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<ProdutoResponse>> findAll(
            @Parameter(description = "Código do produto para consulta exata", example = "PROD-001")
            @RequestParam(value = "codigo", required = false) String codigo
    ) {
        if (codigo != null) {
            return ResponseEntity.ok(List.of(ProdutoResponse.from(produtosService.findByCodigo(codigo))));
        }
        return ResponseEntity.ok(produtosService.findAll().stream().map(ProdutoResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ProdutoResponse> findById(
            @Parameter(description = "Identificador do produto", example = "65f1a2b3c4d5e6f789012346", required = true)
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findById(id)));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca produto por código")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ProdutoResponse> findByCodigo(
            @Parameter(description = "Código do produto", example = "PROD-001", required = true)
            @PathVariable("codigo") String codigo
    ) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findByCodigo(codigo)));
    }

    @PostMapping
    @Operation(summary = "Cria um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Código já cadastrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ProdutoResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do produto", required = true)
            @Valid @RequestBody ProdutoRequest request
    ) {
        Produtos saved = produtosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(ProdutoResponse.from(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Código já cadastrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ProdutoResponse> update(
            @Parameter(description = "Identificador do produto", example = "65f1a2b3c4d5e6f789012346", required = true)
            @PathVariable("id") String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados do produto", required = true)
            @Valid @RequestBody ProdutoRequest request
    ) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.update(id, request.toModel())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do produto", example = "65f1a2b3c4d5e6f789012346", required = true)
            @PathVariable("id") String id
    ) {
        produtosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
