# Arquitetura

## Escopo e estilo arquitetural

O sistema é um monólito modular executado como um único processo Spring Boot. A separação por camadas mantém responsabilidades claras sem introduzir complexidade distribuída:

```text
HTTP/REST → Controller + DTO → Service → Repository → MongoDB
                 ↓
          Exception Handler
```

Os controllers recebem e traduzem requisições HTTP, os services concentram casos de uso e regras de negócio, e os repositories encapsulam o acesso ao MongoDB. Os DTOs impedem que os documentos de persistência sejam usados diretamente como contrato público.

## C4 nível 1 — contexto do sistema

```mermaid
C4Context
    title Contexto — Bootcamp XP Edu

    Person(client, "Consumidor da API", "Cliente HTTP, frontend, integração ou ferramenta de operação")
    System(bootcamp, "Bootcamp XP Edu", "API REST para clientes, produtos e pedidos")
    System_Ext(mongo, "MongoDB", "Banco de documentos para persistência")

    Rel(client, bootcamp, "Gerencia clientes, produtos e pedidos", "HTTPS/JSON")
    Rel(bootcamp, mongo, "Lê e grava documentos", "MongoDB wire protocol")
```

## C4 nível 2 — containers de execução

O sistema possui um container de aplicação e uma dependência de dados. `Controller`, `Service` e `Repository` são componentes internos do container Spring Boot, e não processos implantáveis separados.

```mermaid
C4Container
    title Containers — Bootcamp XP Edu

    Person(client, "Consumidor da API", "Cliente HTTP")

    System_Boundary(system, "Bootcamp XP Edu") {
        Container(api, "API REST", "Java 25 / Spring Boot 4.1.1", "Expõe endpoints versionados, valida requests, executa casos de uso e serializa respostas")
    }

    ContainerDb(mongo, "MongoDB", "MongoDB", "Armazena os documentos das coleções clientes, produtos e pedidos")

    Rel(client, api, "Consome", "HTTP/JSON")
    Rel(api, mongo, "Persiste e consulta", "Spring Data MongoDB")
```

## C4 nível 3 — componentes da aplicação

```mermaid
C4Component
    title Componentes — API REST

    Container_Boundary(api, "API REST — Spring Boot") {
        Component(controllers, "Controllers", "Spring MVC", "Mapeiam endpoints /api/v1 e retornam códigos HTTP")
        Component(dtos, "DTOs", "Java records + Bean Validation", "Validam entradas e definem o contrato de saída")
        Component(services, "Services", "Spring @Service", "Executam casos de uso, regras de unicidade e validação de referências")
        Component(repositories, "Repositories", "Spring Data MongoRepository", "Executam consultas e operações de persistência")
        Component(errors, "GlobalExceptionHandler", "Spring @RestControllerAdvice", "Converte falhas em ApiErrorResponse")
        Component(config, "Configuração", "Spring Configuration", "Habilita auditoria/repositórios MongoDB e metadados OpenAPI")
    }

    ContainerDb(mongo, "MongoDB", "Banco de documentos")

    Rel(controllers, dtos, "Recebe e produz")
    Rel(controllers, services, "Invoca casos de uso")
    Rel(controllers, errors, "Falhas são tratadas por")
    Rel(services, repositories, "Consulta e grava")
    Rel(repositories, mongo, "Lê e grava documentos")
    Rel(config, repositories, "Configura")
```

## Fluxo principal: criação de pedido

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Consumidor HTTP
    participant Controller as PedidosController
    participant Service as PedidosServiceImpl
    participant Clientes as ClientesRepository
    participant Produtos as ProdutosRepository
    participant Pedidos as PedidosRepository
    participant Mongo as MongoDB

    Cliente->>Controller: POST /api/v1/pedidos
    Controller->>Controller: @Valid valida PedidoRequest
    Controller->>Service: save(pedido)
    Service->>Clientes: existsById(clienteId)
    Clientes->>Mongo: consulta clientes
    Mongo-->>Clientes: resultado
    Service->>Produtos: existsById(produtoId) para cada item
    Produtos->>Mongo: consulta produtos
    Mongo-->>Produtos: resultados
    Service->>Pedidos: save(pedido)
    Pedidos->>Mongo: insere documento
    Mongo-->>Pedidos: documento com id
    Pedidos-->>Service: pedido salvo
    Service-->>Controller: entidade persistida
    Controller-->>Cliente: 201 Created + Location + JSON
```

## Decisões e limites atuais

### Consistência de referências

Pedidos usam referências lógicas (`clienteId` e `produtosId`) em vez de embedding dos documentos completos. O service verifica a existência das referências antes da gravação. Essa decisão reduz duplicação e mantém alterações de cliente/produto fora do documento de pedido, mas não cria transação distribuída nem impede que uma entidade referenciada seja removida depois.

### Unicidade

Clientes e produtos fazem consultas de disponibilidade antes de salvar. Como essa checagem é uma operação separada da escrita, a garantia é suscetível a condição de corrida se não houver índices únicos no MongoDB. Os índices recomendados estão documentados em [data-model.md](data-model.md).

### Tratamento de erros

`GlobalExceptionHandler` retorna `ApiErrorResponse` para validação (`400`), recurso ausente (`404`), conflito (`409`), endpoint inexistente (`404`) e falhas inesperadas (`500`). Falhas inesperadas são registradas no log sem expor detalhes internos na resposta.

### Observabilidade

Os services registram início, conclusão, quantidade e falhas das operações em nível `INFO`/`WARN`/`ERROR`. Ainda não há métricas, tracing distribuído, correlação de request ou endpoint de health documentados no projeto.

### Segurança

Não há autenticação ou autorização implementadas no código atual. O serviço deve ser executado atrás de HTTPS e de um mecanismo de identidade/autorização antes de ser disponibilizado fora de um ambiente confiável.

