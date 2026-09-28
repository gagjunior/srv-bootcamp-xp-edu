package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.model.Produtos;

import java.util.List;

/**
 * Casos de uso relacionados a produtos.
 */
public interface ProdutosService {

    List<Produtos> findAll();

    Produtos findById(String id);

    Produtos findByCodigo(String codigo);

    Produtos save(Produtos produto);

    Produtos update(String id, Produtos produto);

    void deleteById(String id);
}
