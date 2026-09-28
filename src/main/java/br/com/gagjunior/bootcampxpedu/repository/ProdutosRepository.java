package br.com.gagjunior.bootcampxpedu.repository;

import br.com.gagjunior.bootcampxpedu.model.Produtos;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository de persistência de produtos.
 */
public interface ProdutosRepository extends MongoRepository<Produtos, String> {

    Optional<Produtos> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
