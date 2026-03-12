# API - Sistema de Gestão de Atividades Complementares - SENAC

<div align="center">

## 🚧 Status do Projeto

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

## 🛠️ Tecnologias Utilizadas

<p>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-Framework-6DB33F?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/SpringSecurity-Security-6DB33F?style=for-the-badge&logo=springsecurity" />
  <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql" />
</p>

</div>

---

> API REST responsável pela **gestão, registro e validação de atividades complementares em ambiente universitário**, desenvolvida como parte de um projeto acadêmico do **SENAC**.

---

## 🏫 Informações Acadêmicas

| Campo               | Descrição                                        |
| ------------------- | ------------------------------------------------ |
| **Instituição**     | SENAC                                            |
| **Curso**           | Análise e Desenvolvimento de Sistemas (ADS)      |
| **Disciplina**      | Projeto Integrador / Desenvolvimento de Sistemas |
| **Semestre**        | 2026.1                                           |
| **Tipo de Projeto** | Sistema Web Acadêmico                            |

---

## 📋 Sobre o Projeto

O **Sistema de Gestão de Atividades Complementares** é uma aplicação desenvolvida para **automatizar o controle de horas complementares exigidas nos cursos de graduação**.

Atividades complementares são componentes obrigatórios definidos pelas **Diretrizes Curriculares Nacionais (DCN)** e incluem diversas experiências acadêmicas, como:

- Participação em congressos e eventos acadêmicos
- Cursos extracurriculares
- Projetos de pesquisa ou extensão
- Monitorias
- Publicações científicas
- Palestras e workshops

Em muitos casos, esse processo é realizado de forma manual, com entrega de documentos físicos e controle por planilhas.

Este sistema propõe uma solução digital integrada que permite:

- Registro de atividades realizadas pelos alunos
- Envio de certificados digitalmente
- Avaliação e validação das atividades por coordenadores
- Controle automático da carga horária acumulada

A API foi desenvolvida utilizando **Spring Boot** e segue o padrão de **arquitetura em camadas**, garantindo organização, escalabilidade e facilidade de manutenção.

---

## 🎯 Objetivos

### Objetivo Geral

Desenvolver uma **API RESTful segura e escalável** para gerenciamento do ciclo de vida das atividades complementares acadêmicas.

### Objetivos Específicos

- Permitir cadastro e gerenciamento de usuários
- Registrar atividades complementares realizadas pelos alunos
- Permitir envio de comprovantes de atividades
- Permitir aprovação ou rejeição das atividades por coordenadores
- Registrar a carga horária obtida em cada atividade
- Garantir rastreabilidade das ações dentro do sistema

---

## 🧩 Perfis de Usuário

O sistema foi projetado para atender três perfis principais.

### 👨‍🎓 Aluno

- Submissão de atividades complementares
- Upload de certificados
- Acompanhamento do status das solicitações
- Visualização da carga horária acumulada

### 👩‍🏫 Coordenador

- Avaliação de atividades enviadas pelos alunos
- Aprovação ou rejeição de certificados
- Definição de regras de validação de atividades
- Acompanhamento do progresso dos alunos

### 🔐 Super Administrador

- Gerenciamento de cursos
- Gerenciamento de coordenadores
- Configuração geral do sistema

---

## 🔄 Fluxo de Submissão de Atividades

```
Aluno envia certificado
        │
        ▼
  ┌───────────┐
  │ PENDENTE  │
  └─────┬─────┘
        │
  Coordenador avalia
        │
  ┌─────┴─────────┐
  ▼               ▼
APROVADA      REPROVADA
```

Todas as mudanças de status são registradas para manter **histórico e transparência no processo de validação**.

---

## 🏗️ Arquitetura do Sistema

A aplicação segue o padrão **Arquitetura em Camadas (Layered Architecture)**.

**Controller** — Responsável por receber requisições HTTP e expor os endpoints da API.

**Service** — Contém as regras de negócio da aplicação.

**Repository** — Responsável pela comunicação com o banco de dados utilizando **Spring Data JPA**.

**Model / Entity** — Representa as entidades persistidas no banco de dados.

Essa arquitetura permite melhor organização do código, separação de responsabilidades e maior facilidade de manutenção.

---

## 🗂️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/pi/apigenatvdcomplementares/
│   │
│   ├── dto/          # Data Transfer Objects
│   ├── enums/        # Enumerações do sistema
│   ├── models/       # Entidades JPA
│   ├── repository/   # Interfaces Spring Data
│   ├── service/      # Regras de negócio
│   ├── controller/   # Endpoints da API
│   │
│   └── ApigenatvdcomplementaresApplication.java
│
└── resources/
    └── application.properties
```

---

---

## 🔐 Segurança

A API utiliza **Spring Security** para garantir segurança na aplicação. Principais mecanismos:

- Autenticação baseada em **JWT**
- Controle de acesso por perfil (**RBAC**)
- Senhas criptografadas com **BCrypt**
- Validação de dados com **Jakarta Bean Validation**

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

- Java 17+
- Maven
- MySQL 8+

### 1. Clonar o repositório

```bash
git clone https://github.com/Jorgefigueredoo/API-Gen-Atvd-Complementares.git
```

### 2. Acessar o diretório

```bash
cd API-Gen-Atvd-Complementares
```

### 3. Criar banco de dados

```sql
CREATE DATABASE api_sistema_senac;
```

### 4. Configurar `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/api_sistema_senac
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 5. Executar a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## 🧪 Testes

Os testes da API podem ser realizados utilizando ferramentas como:

- [Swagger](https://swagger.io/)
- [Postman](https://www.postman.com/)
- [Insomnia](https://insomnia.rest/)

---

## 🚧 Limitações Atuais e Trabalhos Futuros

O projeto encontra-se em fase de desenvolvimento. Algumas melhorias planejadas incluem:

- Documentação automática com Swagger/OpenAPI
- Sistema completo de autenticação com JWT
- Upload de certificados em armazenamento em nuvem
- Paginação e filtros avançados
- Testes automatizados com JUnit e Mockito
- Containerização com Docker
- Integração contínua com GitHub Actions

---

## 🤝 Contribuição

Este é um projeto acadêmico. Contribuições podem ser feitas por meio de issues ou pull requests.

```bash
git checkout -b feature/minha-feature
git commit -m "feat: descrição da feature"
git push origin feature/minha-feature
```

---

## 📄 Licença

Este projeto é desenvolvido para fins **exclusivamente acadêmicos** no curso de Análise e Desenvolvimento de Sistemas do SENAC.

---

## 👥 Autores

<div align="center">

| Integrantes      |
| ---------------- |
| Jorge Figueredo  |
| Vitor Santos     |
| Antônio Vinicius |
| Lucas Vinicius   |

</div>

---

<div align="center">

> _"A educação é a arma mais poderosa que você pode usar para mudar o mundo."_  
> — **Nelson Mandela**

Feito com ☕ e Java para o SENAC · 2026

</div>
