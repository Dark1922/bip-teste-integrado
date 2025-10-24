# Sistema de Gestão de Benefícios

Sistema completo de gestão de benefícios com arquitetura em camadas, CRUD completo, interface Angular e testes.

## Funcionalidades

- Backend Spring Boot com CRUD completo
- Interface Angular responsiva
- EJB com correção de bug de transferência
- Testes unitários e de integração
- Documentação Swagger

## Arquitetura

```
Frontend (Angular) → Backend (Spring Boot) → EJB Service → Database (H2)
```

## Como Executar

### Pré-requisitos
- Java 17+
- Node.js 18+
- Maven 3.8+

### Backend
```bash
cd backend-module
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

### Acessos
- Frontend: http://localhost:4200
- API: http://localhost:8080/api/v1/beneficios
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Correção do Bug no EJB

O método `transfer` no `BeneficioEjbService` tinha problemas de concorrência e validação. Foi corrigido com:

- Validação de saldo suficiente
- Locking otimista para controle de concorrência
- Tratamento de exceções adequado
- Rollback automático em caso de erro

## API Endpoints

### Benefícios
- `GET /api/v1/beneficios` - Listar todos
- `GET /api/v1/beneficios/{id}` - Buscar por ID
- `POST /api/v1/beneficios` - Criar novo
- `PUT /api/v1/beneficios/{id}` - Atualizar
- `DELETE /api/v1/beneficios/{id}` - Excluir

### Transferências
- `POST /api/v1/beneficios/transferir` - Transferir entre benefícios
- `GET /api/v1/beneficios/buscar` - Buscar por nome

## Exemplo de Uso

### Criar Benefício
```json
POST /api/v1/beneficios
{
  "nome": "Benefício Teste",
  "descricao": "Descrição do benefício",
  "valor": 1000.00,
  "ativo": true
}
```

### Transferir Valor
```json
POST /api/v1/beneficios/transferir
{
  "fromId": 1,
  "toId": 2,
  "valor": 100.00
}
```

## Testes

### Backend
```bash
cd backend-module
mvn test
```

### Frontend
```bash
cd frontend
npm test
```

## Estrutura do Projeto

```
bip-teste-integrado/
├── backend-module/          # Spring Boot API
├── ejb-module/             # EJB Business Logic
├── frontend/               # Angular SPA
├── db/                     # Database Scripts
└── docs/                   # Documentation
```

## Configuração do Banco

O sistema usa H2 em memória para desenvolvimento. O schema e dados iniciais são carregados automaticamente.

## Tecnologias

- **Backend**: Spring Boot 3.2.5, JPA/Hibernate
- **Frontend**: Angular 17, Bootstrap 5
- **EJB**: Jakarta EJB 4.0
- **Database**: H2 (desenvolvimento)
- **Testes**: JUnit 5, MockMvc