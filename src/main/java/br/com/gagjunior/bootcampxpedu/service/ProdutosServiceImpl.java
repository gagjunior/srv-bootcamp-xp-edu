package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementação dos casos de uso de produtos.
 */
@Service
public class ProdutosServiceImpl implements ProdutosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProdutosServiceImpl.class);

    private final ProdutosRepository produtosRepository;

    public ProdutosServiceImpl(ProdutosRepository produtosRepository) {
        this.produtosRepository = Objects.requireNonNull(produtosRepository, "produtosRepository não pode ser nulo");
    }

    @Override
    public List<Produtos> findAll() {
        LOGGER.info("Iniciando busca de todos os produtos");
        try {
            List<Produtos> produtos = produtosRepository.findAll();
            LOGGER.info("Busca de todos os produtos concluída. quantidade={}", produtos.size());
            return produtos;
        } catch (RuntimeException exception) {
            logFailure("findAll", exception);
            throw exception;
        }
    }

    @Override
    public Produtos findById(String id) {
        LOGGER.info("Iniciando busca de produto por id. id={}", id);
        try {
            requireText(id, "id");
            Produtos produto = produtosRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
            LOGGER.info("Produto encontrado por id. id={}", id);
            return produto;
        } catch (RuntimeException exception) {
            logFailure("findById", exception);
            throw exception;
        }
    }

    @Override
    public Produtos findByCodigo(String codigo) {
        LOGGER.info("Iniciando busca de produto por código");
        try {
            requireText(codigo, "código");
            Produtos produto = produtosRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto com código", codigo));
            LOGGER.info("Produto encontrado por código");
            return produto;
        } catch (RuntimeException exception) {
            logFailure("findByCodigo", exception);
            throw exception;
        }
    }

    @Override
    public Produtos save(Produtos produto) {
        LOGGER.info("Iniciando criação de produto");
        try {
            Objects.requireNonNull(produto, "produto não pode ser nulo");
            ensureCodigoIsAvailableForCreate(produto.codigo());
            Produtos saved = produtosRepository.save(produto);
            LOGGER.info("Criação de produto concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("save", exception);
            throw exception;
        }
    }

    @Override
    public Produtos update(String id, Produtos produto) {
        LOGGER.info("Iniciando atualização de produto. id={}", id);
        try {
            requireText(id, "id");
            Objects.requireNonNull(produto, "produto não pode ser nulo");
            findById(id);
            ensureCodigoIsAvailable(produto.codigo(), id);

            Produtos updated = new Produtos(id, produto.codigo(), produto.nome(), produto.descricao(), produto.preco());
            Produtos saved = produtosRepository.save(updated);
            LOGGER.info("Atualização de produto concluída. id={}", saved.id());
            return saved;
        } catch (RuntimeException exception) {
            logFailure("update", exception);
            throw exception;
        }
    }

    @Override
    public void deleteById(String id) {
        LOGGER.info("Iniciando exclusão de produto. id={}", id);
        try {
            findById(id);
            produtosRepository.deleteById(id);
            LOGGER.info("Exclusão de produto concluída. id={}", id);
        } catch (RuntimeException exception) {
            logFailure("deleteById", exception);
            throw exception;
        }
    }

    private static void logFailure(String operation, RuntimeException exception) {
        if (exception instanceof ResourceNotFoundException
                || exception instanceof DuplicateResourceException
                || exception instanceof IllegalArgumentException) {
            LOGGER.warn("Operação de produto não concluída. operação={}, tipo={}",
                    operation, exception.getClass().getSimpleName());
            return;
        }
        LOGGER.error("Falha inesperada na operação de produto. operação={}", operation, exception);
    }

    private void ensureCodigoIsAvailable(String codigo, String currentId) {
        requireText(codigo, "código");
        produtosRepository.findByCodigo(codigo).ifPresent(existing -> {
            if (currentId == null || !currentId.equals(existing.id())) {
                throw new DuplicateResourceException("Já existe um produto cadastrado com o código informado");
            }
        });
    }

    private void ensureCodigoIsAvailableForCreate(String codigo) {
        requireText(codigo, "código");
        if (produtosRepository.existsByCodigo(codigo)) {
            throw new DuplicateResourceException("Já existe um produto cadastrado com o código informado");
        }
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " não pode ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " não pode ser vazio");
        }
    }
}
