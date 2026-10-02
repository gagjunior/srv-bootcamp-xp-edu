package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.PedidoRequest;
import br.com.gagjunior.bootcampxpedu.dto.PedidoResponse;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.service.PedidosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class PedidosController implements PedidosSwagger {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @Override
    public ResponseEntity<List<PedidoResponse>> findAll(String clienteId, String status) {
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

    @Override
    public ResponseEntity<PedidoResponse> findById(String id) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.findById(id)));
    }

    @Override
    public ResponseEntity<List<PedidoResponse>> findByClienteId(String clienteId) {
        return ResponseEntity.ok(pedidosService.findByClienteId(clienteId).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @Override
    public ResponseEntity<List<PedidoResponse>> findByStatus(String status) {
        return ResponseEntity.ok(pedidosService.findByStatus(status).stream()
                .map(PedidoResponse::from)
                .toList());
    }

    @Override
    public ResponseEntity<PedidoResponse> create(PedidoRequest request) {
        Pedidos saved = pedidosService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(PedidoResponse.from(saved));
    }

    @Override
    public ResponseEntity<PedidoResponse> update(String id, PedidoRequest request) {
        return ResponseEntity.ok(PedidoResponse.from(pedidosService.update(id, request.toModel())));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        pedidosService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
