# API Gen Atvd Complementares

<p align="center">
  <strong>API REST para gestão de atividades complementares acadêmicas</strong>
</p>

<p align="center">
  Projeto desenvolvido para apoiar o controle, envio, validação e acompanhamento de atividades complementares no ambiente acadêmico.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge" />
  <img src="https://img.shields.io/badge/MySQL-8+-4479A1?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Security-JWT-black?style=for-the-badge" />
  <img src="https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=for-the-badge" />
</p>

---

## Sobre o projeto

A **API Gen Atvd Complementares** é um backend REST criado para digitalizar o processo de gerenciamento de atividades complementares em instituições de ensino.

A proposta do sistema é substituir processos manuais por uma solução centralizada, segura e escalável, permitindo que alunos, coordenadores e administradores acompanhem todo o fluxo de submissão e validação das atividades acadêmicas.

---

## Objetivo

Automatizar o controle de atividades complementares, permitindo:

- cadastro e gerenciamento de usuários
- cadastro de alunos
- gerenciamento de cursos
- gerenciamento de turmas
- envio e controle de certificados
- autenticação segura com JWT
- controle de acesso por perfil
- rastreabilidade e organização do processo acadêmico

---

## Perfis de acesso

O sistema trabalha com três perfis principais:

- **ALUNO**
- **COORDENADOR**
- **SUPER_ADMIN**

Cada perfil possui permissões específicas dentro da aplicação.

---

## Funcionalidades já implementadas

- autenticação com login e geração de token JWT
- controle de acesso com Spring Security
- gerenciamento de usuários
- gerenciamento de alunos
- gerenciamento de cursos
- gerenciamento de turmas
- gerenciamento de certificados
- gerenciamento de coordenadores por curso
- documentação da API com Swagger/OpenAPI

---

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 4**
- **Spring Web MVC**
- **Spring Data JPA**
- **Spring Security**
- **JWT**
- **Jakarta Validation**
- **MySQL**
- **Lombok**
- **Springdoc OpenAPI / Swagger UI**
- **Maven**

---

## Estrutura do projeto

```bash
src/
├── main/
│   ├── java/com/pi/apigenatvdcomplementares/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── models/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   └── ApigenatvdcomplementaresApplication.java
│   └── resources/
│       └── application.properties

## Principais endpoints

### Autenticação

* `POST /api/auth/login`

### Usuários

* `POST /usuarios`
* `GET /usuarios`
* `GET /usuarios/{id}`
* `GET /usuarios/email/{email}`
* `DELETE /usuarios/{id}`

### Alunos

* `POST /alunos`
* `GET /alunos`
* `GET /alunos/{id}`
* `PUT /alunos/{id}`
* `DELETE /alunos/{id}`

### Cursos

* `POST /cursos`
* `PUT /cursos/{id}`
* `GET /cursos/{nome}`

### Turmas

* `POST /turmas`
* `GET /turmas`
* `GET /turmas/{id}`
* `GET /turmas/curso/{cursoId}`
* `PUT /turmas/{id}`
* `DELETE /turmas/{id}`

### Certificados

* `POST /certificados`
* `GET /certificados`
* `GET /certificados/{id}`
* `PATCH /certificados/{id}`
* `DELETE /certificados/{id}`

### Coordenadores por curso

* `POST /coordenadores-cursos`
* `GET /coordenadores-cursos`
* `GET /coordenadores-cursos/{nome}`
* `PUT /coordenadores-cursos/{nome}`
* `DELETE /coordenadores-cursos/{nome}`

---

## Segurança

A aplicação utiliza **Spring Security** com autenticação **stateless** baseada em **JWT**.

### Regras principais

* login público em `/api/auth/login`
* Swagger liberado
* endpoints protegidos por autenticação e perfis
* autorização com regras por `role`
* senhas criptografadas
* filtro JWT para validação do token a cada requisição

---

## Documentação da API

A aplicação possui integração com OpenAPI/Swagger.

Após subir o projeto, acesse:

```bash
http://localhost:8080/swagger-ui/index.html
```

---

## Como executar o projeto

### Pré-requisitos

Antes de começar, você precisa ter instalado:

* Java 17 ou superior
* Maven
* MySQL 8 ou superior

### 1. Clone o repositório

```bash
git clone https://github.com/Jorgefigueredoo/API-Gen-Atvd-Complementares.git
```

### 2. Acesse a pasta do projeto

```bash
cd API-Gen-Atvd-Complementares
```

### 3. Crie o banco de dados

```sql
CREATE DATABASE api_sistema_senac;
```

### 4. Configure o `application.properties`

No arquivo:

```bash
src/main/resources/application.properties
```

adicione ou ajuste as propriedades abaixo:

```properties
spring.application.name=apigenatvdcomplementares

spring.datasource.url=jdbc:mysql://localhost:3306/api_sistema_senac
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=sua_chave_base64
jwt.expiration=86400000
```

Substitua:

* `seu_usuario` pelo usuário do MySQL
* `sua_senha` pela senha do MySQL
* `sua_chave_base64` por uma chave JWT segura em Base64

### 5. Execute a aplicação

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A aplicação estará disponível em:

```bash
http://localhost:8080
```

---

## Exemplo de autenticação

### Requisição

```json
{
  "email": "admin@email.com",
  "senha": "123456"
}
```

### Endpoint

```http
POST /api/auth/login
```

### Resposta esperada

```json
{
  "token": "seu_token_jwt",
  "tipo": "Bearer"
}
```

### Uso do token

```http
Authorization: Bearer seu_token_jwt
```

---

## Arquitetura

O projeto segue uma arquitetura em camadas, com separação de responsabilidades entre:

* **Controller** → exposição dos endpoints
* **Service** → regras de negócio
* **Repository** → acesso a dados
* **Model** → entidades persistidas
* **DTO** → transporte de dados entre camadas
* **Security** → autenticação e autorização

Essa organização facilita manutenção, escalabilidade e evolução do sistema.

---

## Status do projeto

🚧 **Em desenvolvimento**

O projeto já possui base funcional com autenticação, controle de acesso e principais módulos de cadastro e gestão acadêmica.

Melhorias futuras possíveis:

* testes automatizados mais completos
* padronização de respostas e tratamento global de exceções
* paginação e filtros
* upload real de arquivos/certificados
* deploy em nuvem
* pipeline CI/CD
* containerização com Docker

---

## Contexto acadêmico

Projeto desenvolvido no contexto acadêmico do **SENAC**, no curso de **Análise e Desenvolvimento de Sistemas**, como parte das atividades do projeto integrador.

---

## Autores

* **Jorge Figueredo**
* **Vitor Santos**
* **Lucas Vinicius**
* **Renan Souza**
* **Antonio Vinicius**
* **Maria Vitória**

---

## Licença

Este projeto foi desenvolvido para fins acadêmicos.

```
```
