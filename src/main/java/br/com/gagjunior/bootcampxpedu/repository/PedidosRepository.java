package br.com.gagjunior.bootcampxpedu.repository;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de persistência de pedidos.
 */
public interface PedidosRepository extends MongoRepository<Pedidos, String> {

    @Override
    @NullMarked
    Optional<Pedidos> findById(String id);

    List<Pedidos> findByClienteId(String clienteId);

    List<Pedidos> findByStatus(String status);

}
