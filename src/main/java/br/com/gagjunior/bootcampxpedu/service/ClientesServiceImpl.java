package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientesServiceImpl.class);

    private final ClientesRepository clientesRepository;

    public ClientesServiceImpl(ClientesRepository clientesRepository) {
        this.clientesRepository = Objects.requireNonNull(clientesRepository, "clientesRepository não pode ser nulo");
    }

    @Override
    public List<Clientes> findAll() {
        LOGGER.info("Iniciando busca de todos os clientes");
        try {
            List<Clientes> clientes = clientesRepository.findAll();
            LOGGER.info("Busca de todos os clientes concluída. quantidade={}", clientes.size());
            return clientes;
        } catch (RuntimeException exception) {
            logFailure("findAll", exception);
            throw exception;
        }
    }

    @Override
    public Clientes findById(String id) {
        LOGGER.info("Iniciando busca de cliente por id. id={}", id);
        try {
            requireText(id, "id");
            Clientes cliente = clientesRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
            LOGGER.info("Cliente encontrado por id. id={}", id);
            return cliente;
        } catch (RuntimeException exception) {
            logFailure("findById", exception);
            throw exception;
        }
    }

    @Override
    public Clientes findByCpf(String cpf) {
        LOGGER.info("Iniciando busca de cliente por CPF");
        try {
            requireText(cpf, "CPF");
            Clientes cliente = clientesRepository.findByCpf(cpf)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente com CPF", cpf));
            LOGGER.info("Cliente encontrado por CPF");
            return cliente;
        } catch (RuntimeException exception) {
            logFailure("findByCpf", exception);
            throw exception;
        }
    }

    @Override
    public Clientes findByEmail(String email) {
        LOGGER.info("Iniciando busca de cliente por e-mail");
        try {
            requireText(email, "e-mail");
            Clientes cliente = clientesRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente com e-mail", email));
            LOGGER.info("Cliente encontrado por e-mail");
            return cliente;
        } catch (RuntimeException exception) {
            logFailure("findByEmail", exception);
            throw exception;
        }
    }

    @Override
    public Clientes save(Clientes cliente) {
        LOGGER.info("Iniciando criação de cliente");
        try {
            Objects.requireNonNull(cliente, "cliente não pode ser nulo");
            ensureCpfIsAvailableForCreate(cliente.cpf());
            ensureEmailIsAvailableForCreate(cliente.email());
            Clientes saved = clientesRepository.save(cliente);
            LOGGER.info("Criação de cliente concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("save", exception);
            throw exception;
        }
    }

    @Override
    public Clientes update(String id, Clientes cliente) {
        LOGGER.info("Iniciando atualização de cliente. id={}", id);
        try {
            requireText(id, "id");
            Objects.requireNonNull(cliente, "cliente não pode ser nulo");
            findById(id);
            ensureCpfIsAvailable(cliente.cpf(), id);
            ensureEmailIsAvailable(cliente.email(), id);

            Clientes updated = new Clientes(id, cliente.nome(), cliente.cpf(), cliente.email());
            Clientes saved = clientesRepository.save(updated);
            LOGGER.info("Atualização de cliente concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("update", exception);
            throw exception;
        }
    }

    @Override
    public void deleteById(String id) {
        LOGGER.info("Iniciando exclusão de cliente. id={}", id);
        try {
            findById(id);
            clientesRepository.deleteById(id);
            LOGGER.info("Exclusão de cliente concluída. id={}", id);
        } catch (RuntimeException exception) {
            logFailure("deleteById", exception);
            throw exception;
        }
    }

    private static void logFailure(String operation, RuntimeException exception) {
        if (exception instanceof ResourceNotFoundException
                || exception instanceof DuplicateResourceException
                || exception instanceof IllegalArgumentException) {
            LOGGER.warn("Operação de cliente não concluída. operação={}, tipo={}",
                    operation, exception.getClass().getSimpleName());
            return;
        }
        LOGGER.error("Falha inesperada na operação de cliente. operação={}", operation, exception);
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
