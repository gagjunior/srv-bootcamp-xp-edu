# Bootcamp XP Edu

API REST para gerenciamento de clientes, produtos e pedidos. O serviço foi construído como um monólito modular em Spring Boot, com persistência em MongoDB e contrato HTTP documentado com OpenAPI 3.

## Índice

- [Visão geral](#visão-geral)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Como subir o serviço localmente](#como-subir-o-serviço-localmente)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Como visualizar o Swagger](#como-visualizar-o-swagger)
- [Contrato da API](#contrato-da-api)
- [Arquitetura e diagramas](#arquitetura-e-diagramas)
- [Persistência e modelo de dados](#persistência-e-modelo-de-dados)
- [Testes](#testes)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Boas práticas e próximos passos](#boas-práticas-e-próximos-passos)

## Visão geral

O serviço expõe operações de cadastro, consulta, atualização e exclusão para:

- **Clientes**, identificados por CPF e e-mail;
- **Produtos**, identificados por código;
- **Pedidos**, associados a um cliente e a uma lista de produtos.

A API utiliza versionamento explícito no caminho (`/api/v1`) e retorna um contrato padronizado para erros. A validação de entrada ocorre na borda HTTP e as regras de negócio ficam na camada de serviço.

## Tecnologias utilizadas

| Tecnologia | Uso | Versão definida no projeto |
| --- | --- | --- |
| Java | Linguagem e runtime | 25 |
| Spring Boot | Framework da aplicação | 4.1.1 |
| Spring Web MVC | API HTTP/REST | Gerenciada pelo Spring Boot |
| Spring Data MongoDB | Repositórios e persistência | Gerenciada pelo Spring Boot |
| MongoDB | Banco de dados orientado a documentos | Compatível com o driver do projeto |
| Spring Validation | Validação dos payloads | Gerenciada pelo Spring Boot |
| Springdoc OpenAPI | Especificação e UI Swagger | 3.1.1 |
| Maven | Build, testes e execução | Maven 3.9+ recomendado |

## Como subir o serviço localmente

### Pré-requisitos

- JDK 25 configurado no `PATH` e em `JAVA_HOME`;
- Maven 3.9 ou superior;
- MongoDB acessível em `localhost:27017` ou uma URI equivalente;
- PowerShell, Bash ou outro terminal capaz de definir variáveis de ambiente.

### 1. Subir o MongoDB

Exemplo com Docker:

```bash
docker run --name bootcamp-xp-edu-mongodb \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin \
  -p 27017:27017 \
  -d mongo:8
```

Se o container já existir, use `docker start bootcamp-xp-edu-mongodb`.

### 2. Configurar o ambiente

PowerShell:

```powershell
$env:ENV_MONGO_DB_URI = "mongodb://admin:admin@localhost:27017/?tls=false"
$env:ENV_MONGO_DB_DATABASE = "bootcamp-xpeducacao"
```

Bash:

```bash
export ENV_MONGO_DB_URI='mongodb://admin:admin@localhost:27017/?tls=false'
export ENV_MONGO_DB_DATABASE='bootcamp-xpeducacao'
```

O arquivo `.env` existente no repositório é apenas uma referência local e está ignorado pelo Git; o Spring Boot não o carrega automaticamente sem uma ferramenta adicional.

### 3. Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8081`.

Para gerar o artefato executável:

```bash
mvn clean package
java -jar target/bootcamp-xp-edu-1.0.0.jar
```

## Variáveis de ambiente

As propriedades abaixo são obrigatórias porque `application.yaml` não define valores padrão:

| Variável | Obrigatória | Exemplo | Descrição |
| --- | --- | --- | --- |
| `ENV_MONGO_DB_URI` | Sim | `mongodb://admin:admin@localhost:27017/?tls=false` | URI de conexão do MongoDB |
| `ENV_MONGO_DB_DATABASE` | Sim | `bootcamp-xpeducacao` | Nome do database utilizado pela aplicação |

Configurações fixas atualmente definidas no arquivo de aplicação:

- Porta HTTP: `8081`;
- Nome da aplicação: `bootcamp-xp-edu`;
- Criação automática de índices do Spring Data MongoDB habilitada;
- Logs da aplicação em nível `INFO`.

Em ambientes compartilhados, injete as variáveis pelo mecanismo de secrets/configuration management da plataforma. Não versione credenciais nem URI com senha.

## Como visualizar o Swagger

Com a aplicação em execução, abra:

- [Swagger UI](http://localhost:8081/swagger-ui.html)
- [OpenAPI JSON](http://localhost:8081/api-docs)

O contrato está organizado pelas tags `Clientes`, `Produtos` e `Pedidos`. A API não possui autenticação implementada neste momento; em ambientes não locais, a exposição deve ser protegida por autenticação/autorização e HTTPS.

## Contrato da API

O inventário dos endpoints, payloads, códigos HTTP e contrato de erro está em [docs/api.md](docs/api.md). O Swagger/OpenAPI exposto em runtime é a referência operacional do contrato.

## Arquitetura e diagramas

Os diagramas C4 e as decisões arquiteturais estão em [docs/architecture.md](docs/architecture.md). O documento descreve o contexto, os containers de execução, os componentes internos e o fluxo de uma requisição.

## Persistência e modelo de dados

O modelo orientado a documentos, as referências entre coleções, exemplos de documentos e os índices recomendados estão em [docs/data-model.md](docs/data-model.md).

Pedidos armazenam `clienteId` e `produtosId` como referências lógicas, sem `JOIN` ou chave estrangeira do MongoDB. A aplicação valida a existência dessas referências antes de gravar o pedido.

## Testes

Executar toda a suíte:

```bash
mvn test
```

Executar build completo:

```bash
mvn clean verify
```

Os testes existentes cobrem controllers e services. Testes que dependem do contexto da aplicação precisam de um MongoDB de teste ou de uma configuração de infraestrutura equivalente ao ambiente de execução.

## Estrutura do projeto

```text
src/main/java/br/com/gagjunior/bootcampxpedu/
├── config/        # Configuração do MongoDB e metadados OpenAPI
├── controller/    # Entradas HTTP e contrato documentado da API
├── dto/           # Requests, responses e contrato de erro
├── exception/     # Exceções de domínio e tratamento global
├── model/         # Documentos MongoDB
├── repository/    # Interfaces Spring Data MongoDB
└── service/       # Casos de uso e regras de negócio
docs/
├── api.md
├── architecture.md
└── data-model.md
```

## Boas práticas e próximos passos

- Criar índices únicos para `clientes.cpf`, `clientes.email` e `produtos.codigo`, garantindo unicidade também sob concorrência;
- Adicionar autenticação, autorização e definição de limites de requisição antes de expor a API publicamente;
- Padronizar status de pedido com enum ou catálogo versionado, evitando valores livres;
- Usar `BigDecimal` para valores monetários e definir uma política explícita de arredondamento;
- Adicionar paginação e limites de tamanho às operações de listagem;
- Adicionar health checks, métricas, tracing e correlação de requisições;
- Automatizar validação dos diagramas e da documentação no pipeline de CI.

