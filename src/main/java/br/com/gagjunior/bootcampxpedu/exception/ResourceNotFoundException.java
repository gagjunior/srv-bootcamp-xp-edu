package br.com.gagjunior.bootcampxpedu.exception;

/**
 * Indica que o recurso solicitado não existe na base de dados.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String identifier) {
        super("Recurso %s não encontrado: %s".formatted(resourceName, identifier));
    }
}
