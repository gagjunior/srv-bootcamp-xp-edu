package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.model.Pedidos;

import java.util.List;

/**
 * Casos de uso relacionados a pedidos.
 */
public interface PedidosService {

    List<Pedidos> findAll();

    Pedidos findById(String id);

    List<Pedidos> findByClienteId(String clienteId);

    List<Pedidos> findByStatus(String status);

    Pedidos save(Pedidos pedido);

    Pedidos update(String id, Pedidos pedido);

    void deleteById(String id);
}
