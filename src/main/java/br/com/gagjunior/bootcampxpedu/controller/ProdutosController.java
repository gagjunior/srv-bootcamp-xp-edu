package br.com.gagjunior.bootcampxpedu.controller;

import br.com.gagjunior.bootcampxpedu.dto.ProdutoRequest;
import br.com.gagjunior.bootcampxpedu.dto.ProdutoResponse;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.service.ProdutosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutosController {

    private final ProdutosService produtosService;

    public ProdutosController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> findAll(@RequestParam(required = false) String codigo) {
        if (codigo != null) {
            return ResponseEntity.ok(List.of(ProdutoResponse.from(produtosService.findByCodigo(codigo))));
        }
        return ResponseEntity.ok(produtosService.findAll().stream().map(ProdutoResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findById(id)));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProdutoResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findByCodigo(codigo)));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> create(@Valid @RequestBody ProdutoRequest request) {
        Produtos saved = produtosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(ProdutoResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProdutoRequest request
    ) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.update(id, request.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        produtosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
