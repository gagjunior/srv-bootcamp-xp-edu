package br.com.gagjunior.bootcampxpedu.controller.api.v1;

import br.com.gagjunior.bootcampxpedu.dto.ClienteRequest;
import br.com.gagjunior.bootcampxpedu.dto.ClienteResponse;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.service.ClientesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ClientesController implements ClientesSwagger {

    private final ClientesService clientesService;

    public ClientesController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    @Override
    public ResponseEntity<List<ClienteResponse>> findAll(String cpf, String email) {
        if (cpf != null && email != null) {
            throw new IllegalArgumentException("informe apenas um dos filtros: cpf ou email");
        }

        if (cpf != null) {
            return ResponseEntity.ok(List.of(ClienteResponse.from(clientesService.findByCpf(cpf))));
        }
        if (email != null) {
            return ResponseEntity.ok(List.of(ClienteResponse.from(clientesService.findByEmail(email))));
        }

        return ResponseEntity.ok(clientesService.findAll().stream().map(ClienteResponse::from).toList());
    }

    @Override
    public ResponseEntity<ClienteResponse> findById(String id) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findById(id)));
    }

    @Override
    public ResponseEntity<ClienteResponse> findByCpf(String cpf) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findByCpf(cpf)));
    }

    @Override
    public ResponseEntity<ClienteResponse> findByEmail(String email) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findByEmail(email)));
    }

    @Override
    public ResponseEntity<ClienteResponse> create(ClienteRequest request) {
        Clientes saved = clientesService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(ClienteResponse.from(saved));
    }

    @Override
    public ResponseEntity<ClienteResponse> update(String id, ClienteRequest request) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.update(id, request.toModel())));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        clientesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
