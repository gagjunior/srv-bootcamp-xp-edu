package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import br.com.gagjunior.bootcampxpedu.repository.PedidosRepository;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
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
        return pedidosRepository.findAll();
    }

    @Override
    public Pedidos findById(String id) {
        requireText(id, "id");
        return pedidosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    @Override
    public List<Pedidos> findByClienteId(String clienteId) {
        requireText(clienteId, "clienteId");
        return pedidosRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Pedidos> findByStatus(String status) {
        requireText(status, "status");
        return pedidosRepository.findByStatus(status);
    }

    @Override
    public Pedidos save(Pedidos pedido) {
        validateReferences(pedido);
        return pedidosRepository.save(pedido);
    }

    @Override
    public Pedidos update(String id, Pedidos pedido) {
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
        return pedidosRepository.save(updated);
    }

    @Override
    public void deleteById(String id) {
        findById(id);
        pedidosRepository.deleteById(id);
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
