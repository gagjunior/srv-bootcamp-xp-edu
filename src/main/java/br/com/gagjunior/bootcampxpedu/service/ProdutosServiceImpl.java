package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementação dos casos de uso de produtos.
 */
@Service
public class ProdutosServiceImpl implements ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosServiceImpl(ProdutosRepository produtosRepository) {
        this.produtosRepository = Objects.requireNonNull(produtosRepository, "produtosRepository não pode ser nulo");
    }

    @Override
    public List<Produtos> findAll() {
        return produtosRepository.findAll();
    }

    @Override
    public Produtos findById(String id) {
        requireText(id, "id");
        return produtosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }

    @Override
    public Produtos findByCodigo(String codigo) {
        requireText(codigo, "código");
        return produtosRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Produto com código", codigo));
    }

    @Override
    public Produtos save(Produtos produto) {
        Objects.requireNonNull(produto, "produto não pode ser nulo");
        ensureCodigoIsAvailableForCreate(produto.codigo());
        return produtosRepository.save(produto);
    }

    @Override
    public Produtos update(String id, Produtos produto) {
        requireText(id, "id");
        Objects.requireNonNull(produto, "produto não pode ser nulo");
        findById(id);
        ensureCodigoIsAvailable(produto.codigo(), id);

        Produtos updated = new Produtos(id, produto.codigo(), produto.nome(), produto.descricao(), produto.preco());
        return produtosRepository.save(updated);
    }

    @Override
    public void deleteById(String id) {
        findById(id);
        produtosRepository.deleteById(id);
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
