package br.com.gagjunior.bootcampxpedu.dto;

/** Erro de validação associado a um campo da requisição. */
public record ApiFieldError(String field, String message) {
}
