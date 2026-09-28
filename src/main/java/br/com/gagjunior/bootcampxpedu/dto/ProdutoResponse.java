package br.com.gagjunior.bootcampxpedu.dto;

import br.com.gagjunior.bootcampxpedu.model.Produtos;

/** Representação pública de um produto. */
public record ProdutoResponse(
        String id,
        String codigo,
        String nome,
        String descricao,
        Double preco
) {

    public static ProdutoResponse from(Produtos produto) {
        return new ProdutoResponse(
                produto.id(), produto.codigo(), produto.nome(), produto.descricao(), produto.preco());
    }
}
