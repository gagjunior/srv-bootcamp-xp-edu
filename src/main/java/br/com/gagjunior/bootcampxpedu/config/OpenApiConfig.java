package br.com.gagjunior.bootcampxpedu.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados globais da especificação OpenAPI da API.
 *
 * <p>A UI e os endpoints de documentação são providos pelo starter do springdoc.
 * Esta classe mantém apenas informações próprias do contrato da aplicação.</p>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bootcamp XP Edu API",
                version = "v1",
                description = "API REST para gerenciamento de clientes, produtos e pedidos.",
                contact = @Contact(name = "Bootcamp XP Edu"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class OpenApiConfig {
}
