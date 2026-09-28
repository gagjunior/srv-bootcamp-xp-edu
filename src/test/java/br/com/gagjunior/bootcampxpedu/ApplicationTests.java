package br.com.gagjunior.bootcampxpedu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Verifica se o contexto do Spring Boot e o MongoTemplate inicializam corretamente")
    void contextLoads() {
        assertNotNull(mongoTemplate, "MongoTemplate deve ser autoconfigurado e injetado pelo Spring Boot");
        assertNotNull(mongoTemplate.getDb(), "Conexão com a base de dados do MongoDB deve estar ativa");
    }

}
