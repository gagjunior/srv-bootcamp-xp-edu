package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import br.com.gagjunior.bootcampxpedu.repository.PedidosRepository;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementação dos casos de uso de pedidos.
 *
 * <p>Além da persistência do pedido, valida as referências para que não sejam
 * gravados pedidos apontando para clientes ou produtos inexistentes.</p>
 */
@Service
public class PedidosServiceImpl implements PedidosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedidosServiceImpl.class);

    private final PedidosRepository pedidosRepository;
    private final ClientesRepository clientesRepository;
    private final ProdutosRepository produtosRepository;

    public PedidosServiceImpl(
            PedidosRepository pedidosRepository,
            ClientesRepository clientesRepository,
            ProdutosRepository produtosRepository
    ) {
        this.pedidosRepository = Objects.requireNonNull(pedidosRepository, "pedidosRepository não pode ser nulo");
        this.clientesRepository = Objects.requireNonNull(clientesRepository, "clientesRepository não pode ser nulo");
        this.produtosRepository = Objects.requireNonNull(produtosRepository, "produtosRepository não pode ser nulo");
    }

    @Override
    public List<Pedidos> findAll() {
        LOGGER.info("Iniciando busca de todos os pedidos");
        try {
            List<Pedidos> pedidos = pedidosRepository.findAll();
            LOGGER.info("Busca de todos os pedidos concluída. quantidade={}", pedidos.size());
            return pedidos;
        } catch (RuntimeException exception) {
            logFailure("findAll", exception);
            throw exception;
        }
    }

    @Override
    public Pedidos findById(String id) {
        LOGGER.info("Iniciando busca de pedido por id. id={}", id);
        try {
            requireText(id, "id");
            Pedidos pedido = pedidosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
            LOGGER.info("Pedido encontrado por id. id={}", id);
            return pedido;
        } catch (RuntimeException exception) {
            logFailure("findById", exception);
            throw exception;
        }
    }

    @Override
    public List<Pedidos> findByClienteId(String clienteId) {
        LOGGER.info("Iniciando busca de pedidos por cliente. clienteId={}", clienteId);
        try {
            requireText(clienteId, "clienteId");
            List<Pedidos> pedidos = pedidosRepository.findByClienteId(clienteId);
            LOGGER.info("Busca de pedidos por cliente concluída. clienteId={}, quantidade={}", clienteId, pedidos.size());
            return pedidos;
        } catch (RuntimeException exception) {
            logFailure("findByClienteId", exception);
            throw exception;
        }
    }

    @Override
    public List<Pedidos> findByStatus(String status) {
        LOGGER.info("Iniciando busca de pedidos por status. status={}", status);
        try {
            requireText(status, "status");
            List<Pedidos> pedidos = pedidosRepository.findByStatus(status);
            LOGGER.info("Busca de pedidos por status concluída. status={}, quantidade={}", status, pedidos.size());
            return pedidos;
        } catch (RuntimeException exception) {
            logFailure("findByStatus", exception);
            throw exception;
        }
    }

    @Override
    public Pedidos save(Pedidos pedido) {
        LOGGER.info("Iniciando criação de pedido");
        try {
            validateReferences(pedido);
            Pedidos saved = pedidosRepository.save(pedido);
            LOGGER.info("Criação de pedido concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("save", exception);
            throw exception;
        }
    }

    @Override
    public Pedidos update(String id, Pedidos pedido) {
        LOGGER.info("Iniciando atualização de pedido. id={}", id);
        try {
            requireText(id, "id");
            findById(id);
            validateReferences(pedido);

            Pedidos updated = new Pedidos(
                    id,
                    pedido.clienteId(),
                    pedido.produtosId(),
                    pedido.valorTotal(),
                    pedido.status()
            );
            Pedidos saved = pedidosRepository.save(updated);
            LOGGER.info("Atualização de pedido concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("update", exception);
            throw exception;
        }
    }

    @Override
    public void deleteById(String id) {
        LOGGER.info("Iniciando exclusão de pedido. id={}", id);
        try {
            findById(id);
            pedidosRepository.deleteById(id);
            LOGGER.info("Exclusão de pedido concluída. id={}", id);
        } catch (RuntimeException exception) {
            logFailure("deleteById", exception);
            throw exception;
        }
    }

    private static void logFailure(String operation, RuntimeException exception) {
        if (exception instanceof ResourceNotFoundException
                || exception instanceof IllegalArgumentException) {
            LOGGER.warn("Operação de pedido não concluída. operação={}, tipo={}",
                    operation, exception.getClass().getSimpleName());
            return;
        }
        LOGGER.error("Falha inesperada na operação de pedido. operação={}", operation, exception);
    }

    private void validateReferences(Pedidos pedido) {
        Objects.requireNonNull(pedido, "pedido não pode ser nulo");
        requireText(pedido.clienteId(), "clienteId");
        requireText(pedido.status(), "status");

        if (pedido.produtosId() == null || pedido.produtosId().isEmpty()) {
            throw new IllegalArgumentException("produtosId não pode ser nulo ou vazio");
        }
        if (pedido.valorTotal() == null || pedido.valorTotal() < 0) {
            throw new IllegalArgumentException("valorTotal não pode ser nulo ou negativo");
        }
        if (!clientesRepository.existsById(pedido.clienteId())) {
            throw new ResourceNotFoundException("Cliente referenciado pelo pedido", pedido.clienteId());
        }

        pedido.produtosId().forEach(produtoId -> {
            requireText(produtoId, "produtoId");
            if (!produtosRepository.existsById(produtoId)) {
                throw new ResourceNotFoundException("Produto referenciado pelo pedido", produtoId);
            }
        });
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " não pode ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " não pode ser vazio");
        }
    }
}
