package br.com.gagjunior.bootcampxpedu.exception;

/**
 * Indica que uma operação violaria uma regra de unicidade do domínio.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
