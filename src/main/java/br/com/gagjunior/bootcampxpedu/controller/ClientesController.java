package br.com.gagjunior.bootcampxpedu.controller;

import br.com.gagjunior.bootcampxpedu.dto.ClienteRequest;
import br.com.gagjunior.bootcampxpedu.dto.ClienteResponse;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.service.ClientesService;
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
@RequestMapping("/api/v1/clientes")
public class ClientesController {

    private final ClientesService clientesService;

    public ClientesController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll(
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email
    ) {
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

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findById(id)));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findByCpf(cpf)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.findByEmail(email)));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        Clientes saved = clientesService.save(request.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(ClienteResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ClienteRequest request
    ) {
        return ResponseEntity.ok(ClienteResponse.from(clientesService.update(id, request.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clientesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
