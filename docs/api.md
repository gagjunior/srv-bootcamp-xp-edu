# API HTTP

## Informações gerais

- Base local: `http://localhost:8081`
- Versão: `/api/v1`
- Formato: `application/json`
- Autenticação: não implementada atualmente
- Contrato gerado: [OpenAPI JSON](http://localhost:8081/api-docs)

As operações de criação e atualização recebem JSON. Criações retornam `201 Created` com o header `Location`; exclusões bem-sucedidas retornam `204 No Content`.

## Endpoints

### Clientes

| Método | Rota | Descrição | Respostas de sucesso |
| --- | --- | --- | --- |
| `GET` | `/api/v1/clientes` | Lista todos; aceita `cpf` ou `email` como filtro | `200` |
| `GET` | `/api/v1/clientes/{id}` | Busca por ID | `200` |
| `GET` | `/api/v1/clientes/cpf/{cpf}` | Busca por CPF | `200` |
| `GET` | `/api/v1/clientes/email/{email}` | Busca por e-mail | `200` |
| `POST` | `/api/v1/clientes` | Cria cliente | `201` |
| `PUT` | `/api/v1/clientes/{id}` | Atualiza cliente | `200` |
| `DELETE` | `/api/v1/clientes/{id}` | Exclui cliente | `204` |

Payload de criação/atualização:

```json
{
  "nome": "Ana Silva",
  "cpf": "12345678900",
  "email": "ana.silva@example.com"
}
```

Os filtros `cpf` e `email` do endpoint de lista são mutuamente exclusivos. Quando um filtro é usado, a resposta continua sendo uma lista, com zero ou um item.

### Produtos

| Método | Rota | Descrição | Respostas de sucesso |
| --- | --- | --- | --- |
| `GET` | `/api/v1/produtos` | Lista todos; aceita `codigo` como filtro | `200` |
| `GET` | `/api/v1/produtos/{id}` | Busca por ID | `200` |
| `GET` | `/api/v1/produtos/codigo/{codigo}` | Busca por código | `200` |
| `POST` | `/api/v1/produtos` | Cria produto | `201` |
| `PUT` | `/api/v1/produtos/{id}` | Atualiza produto | `200` |
| `DELETE` | `/api/v1/produtos/{id}` | Exclui produto | `204` |

Payload de criação/atualização:

```json
{
  "codigo": "PROD-001",
  "nome": "Teclado mecânico",
  "descricao": "Teclado mecânico ABNT2 com iluminação RGB",
  "preco": 249.90
}
```

### Pedidos

| Método | Rota | Descrição | Respostas de sucesso |
| --- | --- | --- | --- |
| `GET` | `/api/v1/pedidos` | Lista todos; aceita `clienteId` ou `status` como filtro | `200` |
| `GET` | `/api/v1/pedidos/{id}` | Busca por ID | `200` |
| `GET` | `/api/v1/pedidos/cliente/{clienteId}` | Busca por cliente | `200` |
| `GET` | `/api/v1/pedidos/status/{status}` | Busca por status | `200` |
| `POST` | `/api/v1/pedidos` | Cria pedido | `201` |
| `PUT` | `/api/v1/pedidos/{id}` | Atualiza pedido | `200` |
| `DELETE` | `/api/v1/pedidos/{id}` | Exclui pedido | `204` |

Payload de criação/atualização:

```json
{
  "clienteId": "65f1a2b3c4d5e6f789012345",
  "produtosId": [
    "65f1a2b3c4d5e6f789012346"
  ],
  "valorTotal": 249.90,
  "status": "RECEBIDO"
}
```

Os filtros `clienteId` e `status` do endpoint de lista são mutuamente exclusivos. Na criação/atualização, o service valida a existência do cliente e de todos os produtos referenciados.

## Códigos de erro

| HTTP | Situação |
| --- | --- |
| `400 Bad Request` | JSON inválido, campo obrigatório ausente, valor inválido ou filtros incompatíveis |
| `404 Not Found` | Recurso, referência ou endpoint não encontrado |
| `409 Conflict` | CPF, e-mail, código ou outro valor único já utilizado |
| `500 Internal Server Error` | Falha inesperada no servidor |

Formato comum de erro:

```json
{
  "timestamp": "2026-10-01T23:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "A requisição possui campos inválidos",
  "path": "/api/v1/clientes",
  "errors": [
    {
      "field": "email",
      "message": "email deve possuir um formato válido"
    }
  ]
}
```

`errors` é omitido quando não há erros por campo. O timestamp é retornado em UTC.

## Exemplo rápido

```bash
curl -i -X POST http://localhost:8081/api/v1/clientes \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Ana Silva","cpf":"12345678900","email":"ana.silva@example.com"}'
```

