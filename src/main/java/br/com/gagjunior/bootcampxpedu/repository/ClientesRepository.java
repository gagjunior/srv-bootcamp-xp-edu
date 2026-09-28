package br.com.gagjunior.bootcampxpedu.repository;

import br.com.gagjunior.bootcampxpedu.model.Clientes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository de persistência de clientes.
 *
 * <p>A implementação é gerada pelo Spring Data MongoDB em tempo de execução.
 * Regras de negócio devem permanecer na camada de serviço.</p>
 */
public interface ClientesRepository extends MongoRepository<Clientes, String> {

    Optional<Clientes> findByCpf(String cpf);

    Optional<Clientes> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
