package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.ProdutoRequest;
import br.com.gagjunior.bootcampxpedu.dto.ProdutoResponse;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.service.ProdutosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ProdutosController implements ProdutosSwagger {

    private final ProdutosService produtosService;

    public ProdutosController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @Override
    public ResponseEntity<List<ProdutoResponse>> findAll(String codigo) {
        if (codigo != null) {
            return ResponseEntity.ok(List.of(ProdutoResponse.from(produtosService.findByCodigo(codigo))));
        }
        return ResponseEntity.ok(produtosService.findAll().stream().map(ProdutoResponse::from).toList());
    }

    @Override
    public ResponseEntity<ProdutoResponse> findById(String id) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findById(id)));
    }

    @Override
    public ResponseEntity<ProdutoResponse> findByCodigo(String codigo) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.findByCodigo(codigo)));
    }

    @Override
    public ResponseEntity<ProdutoResponse> create(ProdutoRequest request) {
        Produtos saved = produtosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(ProdutoResponse.from(saved));
    }

    @Override
    public ResponseEntity<ProdutoResponse> update(String id, ProdutoRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(produtosService.update(id, request.toModel())));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        produtosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
