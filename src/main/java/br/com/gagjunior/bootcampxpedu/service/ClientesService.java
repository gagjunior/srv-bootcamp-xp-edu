package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.model.Clientes;

import java.util.List;

/**
 * Casos de uso relacionados a clientes.
 */
public interface ClientesService {

    List<Clientes> findAll();

    Clientes findById(String id);

    Clientes findByCpf(String cpf);

    Clientes findByEmail(String email);

    Clientes save(Clientes cliente);

    Clientes update(String id, Clientes cliente);

    void deleteById(String id);
}
