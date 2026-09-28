package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementação dos casos de uso de clientes.
 *
 * <p>A classe concentra validações de existência e unicidade. O repository
 * permanece responsável apenas pelo acesso aos dados.</p>
 */
@Service
public class ClientesServiceImpl implements ClientesService {

    private final ClientesRepository clientesRepository;

    public ClientesServiceImpl(ClientesRepository clientesRepository) {
        this.clientesRepository = Objects.requireNonNull(clientesRepository, "clientesRepository não pode ser nulo");
    }

    @Override
    public List<Clientes> findAll() {
        return clientesRepository.findAll();
    }

    @Override
    public Clientes findById(String id) {
        requireText(id, "id");
        return clientesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    @Override
    public Clientes findByCpf(String cpf) {
        requireText(cpf, "CPF");
        return clientesRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com CPF", cpf));
    }

    @Override
    public Clientes findByEmail(String email) {
        requireText(email, "e-mail");
        return clientesRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com e-mail", email));
    }

    @Override
    public Clientes save(Clientes cliente) {
        Objects.requireNonNull(cliente, "cliente não pode ser nulo");
        ensureCpfIsAvailableForCreate(cliente.cpf());
        ensureEmailIsAvailableForCreate(cliente.email());
        return clientesRepository.save(cliente);
    }

    @Override
    public Clientes update(String id, Clientes cliente) {
        requireText(id, "id");
        Objects.requireNonNull(cliente, "cliente não pode ser nulo");
        findById(id);
        ensureCpfIsAvailable(cliente.cpf(), id);
        ensureEmailIsAvailable(cliente.email(), id);

        Clientes updated = new Clientes(id, cliente.nome(), cliente.cpf(), cliente.email());
        return clientesRepository.save(updated);
    }

    @Override
    public void deleteById(String id) {
        findById(id);
        clientesRepository.deleteById(id);
    }

    private void ensureCpfIsAvailable(String cpf, String currentId) {
        requireText(cpf, "CPF");
        clientesRepository.findByCpf(cpf).ifPresent(existing -> {
            if (!isSameResource(existing.id(), currentId)) {
                throw new DuplicateResourceException("Já existe um cliente cadastrado com o CPF informado");
            }
        });
    }

    private void ensureCpfIsAvailableForCreate(String cpf) {
        requireText(cpf, "CPF");
        if (clientesRepository.existsByCpf(cpf)) {
            throw new DuplicateResourceException("Já existe um cliente cadastrado com o CPF informado");
        }
    }

    private void ensureEmailIsAvailable(String email, String currentId) {
        requireText(email, "e-mail");
        clientesRepository.findByEmail(email).ifPresent(existing -> {
            if (!isSameResource(existing.id(), currentId)) {
                throw new DuplicateResourceException("Já existe um cliente cadastrado com o e-mail informado");
            }
        });
    }

    private void ensureEmailIsAvailableForCreate(String email) {
        requireText(email, "e-mail");
        if (clientesRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Já existe um cliente cadastrado com o e-mail informado");
        }
    }

    private boolean isSameResource(String existingId, String currentId) {
        return currentId != null && currentId.equals(existingId);
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " não pode ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " não pode ser vazio");
        }
    }
}
