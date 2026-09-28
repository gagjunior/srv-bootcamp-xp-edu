package br.com.gagjunior.bootcampxpedu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuração da integração com o MongoDB e auditoria do Spring Data.
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "br.com.gagjunior.bootcampxpedu.repository")
public class MongoConfig {
}
