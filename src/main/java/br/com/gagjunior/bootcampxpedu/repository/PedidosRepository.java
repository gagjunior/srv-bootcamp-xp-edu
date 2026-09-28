package br.com.gagjunior.bootcampxpedu.repository;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository de persistência de pedidos.
 */
public interface PedidosRepository extends MongoRepository<Pedidos, String> {

    List<Pedidos> findByClienteId(String clienteId);

    List<Pedidos> findByStatus(String status);

}
