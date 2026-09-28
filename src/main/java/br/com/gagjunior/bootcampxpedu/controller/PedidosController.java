package br.com.gagjunior.bootcampxpedu.controller;

import br.com.gagjunior.bootcampxpedu.dto.PedidoRequest;
import br.com.gagjunior.bootcampxpedu.dto.PedidoResponse;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.service.PedidosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidosController {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll(
            @RequestParam(required = false) String clienteId,
            @RequestParam(required = false) String status
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
    public ResponseEntity<PedidoResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.findById(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> findByClienteId(@PathVariable String clienteId) {
        return ResponseEntity.ok(pedidosService.findByClienteId(clienteId).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoResponse>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(pedidosService.findByStatus(status).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest request) {
        Pedidos saved = pedidosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(PedidoResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(
            @PathVariable String id,
            @Valid @RequestBody PedidoRequest request
    ) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.update(id, request.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        pedidosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
