# API Gen Atvd Complementares — SENAC

<div align="center">

![Status](https://img.shields.io/badge/Status-Em%20Produção-green?style=for-the-badge)
![Deploy](https://img.shields.io/badge/Deploy-Render-46E3B7?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-red?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8+-4479A1?style=for-the-badge)

**🔗 [API em produção](https://api-senac-5zz7.onrender.com)**  
**📄 [Documentação Swagger](https://api-senac-5zz7.onrender.com/swagger-ui/index.html)**

</div>

---

> API REST para gestão completa de atividades complementares acadêmicas, com autenticação JWT, controle de acesso por perfil, envio de e-mails transacionais e regras de negócio configuráveis.

---

## 🏫 Contexto Acadêmico

| Campo | Descrição |
|---|---|
| **Instituição** | SENAC |
| **Curso** | Análise e Desenvolvimento de Sistemas (ADS) |
| **Disciplina** | Projeto Integrador |
| **Semestre** | 2026.1 |

---

## 🛠️ Tecnologias

| Tecnologia | Finalidade |
|---|---|
| Java 17 | Linguagem principal |
| Spring Boot 4 | Framework web |
| Spring Security + JWT | Autenticação e autorização |
| Spring Data JPA + Hibernate | Persistência de dados |
| MySQL 8 (Aiven Cloud) | Banco de dados em produção |
| SendGrid HTTP API | Envio de e-mails transacionais |
| Springdoc OpenAPI / Swagger UI | Documentação da API |
| Lombok | Redução de boilerplate |
| Maven | Gerenciamento de dependências |
| Docker | Containerização para deploy |

---

## 📋 Funcionalidades Implementadas

### Autenticação
- Login com geração de token JWT
- Filtro JWT para validação em cada requisição
- Recuperação de senha com código de 6 dígitos por e-mail (expira em 15 minutos)
- Senhas criptografadas com BCrypt

### Gestão de Usuários
- CRUD completo de usuários
- Perfis: `ALUNO`, `COORDENADOR`, `SUPER_ADMIN`

### Gestão de Alunos
- Cadastro com matrícula e vínculo a curso
- Endpoint `/alunos/me` — aluno consulta os próprios dados
- Endpoint `/alunos/me/cursos` — retorna apenas os cursos do aluno logado

### Gestão de Cursos
- CRUD de cursos com carga horária mínima
- Carga horária atualizada automaticamente pela soma das regras de atividades

### Gestão de Turmas
- CRUD de turmas por curso
- Vínculo/desvinculação de alunos com validação de matrícula no curso
- Permissão: coordenador só cria/edita turmas dos próprios cursos

### Gestão de Coordenadores
- Vínculo coordenador ↔ curso
- Regra de negócio: 1 coordenador por curso / 1 coordenador pode ter N cursos

### Submissões
- Criação de submissão por aluno
- Aprovação e reprovação pelo coordenador com feedback
- Histórico de status por submissão

### Certificados
- Armazenamento de comprovantes em base64 (LONGTEXT)
- Vínculo com submissão

### Regras de Atividades
- CRUD de regras por curso (área, limite de horas, itens, exige comprovante)
- Ao salvar/deletar uma regra, recalcula e atualiza automaticamente a carga horária mínima do curso

### E-mails Transacionais (SendGrid)
- Confirmação de submissão recebida
- Notificação de aprovação com feedback
- Notificação de reprovação com motivo
- Código de recuperação de senha
- Confirmação de senha alterada
- Templates HTML responsivos com nome do curso dinâmico

---

## 🔐 Perfis e Permissões

| Endpoint | ALUNO | COORDENADOR | SUPER_ADMIN |
|---|:---:|:---:|:---:|
| `GET /alunos/me` | ✅ | — | — |
| `GET /alunos/me/cursos` | ✅ | — | — |
| `/alunos/**` | — | ✅ | ✅ |
| `GET /cursos` | ✅ | ✅ | ✅ |
| `POST/PUT/DELETE /cursos` | — | — | ✅ |
| `/turmas/**` | — | ✅ | ✅ |
| `GET /submissoes` | ✅ | ✅ | ✅ |
| `POST /submissoes` | ✅ | ✅ | ✅ |
| `PATCH /submissoes/{id}/aprovar` | — | ✅ | ✅ |
| `PATCH /submissoes/{id}/rejeitar` | — | ✅ | ✅ |
| `/regras/**` | GET | ✅ | ✅ |
| `/coordenadores-cursos/**` | — | — | ✅ |
| `/auth/password/**` | público | público | público |

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/pi/apigenatvdcomplementares/
├── config/
│   ├── SecurityConfig.java       # Regras de autorização por endpoint
│   ├── CorsConfig.java           # CORS com origens via variável de ambiente
│   └── AsyncConfig.java          # @EnableAsync para e-mails assíncronos
├── controller/
│   ├── AuthController.java
│   ├── UsuarioController.java
│   ├── AlunoController.java
│   ├── CursoController.java
│   ├── TurmaController.java      # Inclui endpoints de vínculo de alunos
│   ├── SubmissaoController.java
│   ├── CertificadoController.java
│   ├── CoordenadorController.java
│   ├── RegraAtividadeController.java
│   └── PasswordResetController.java
├── service/
│   ├── EmailService.java         # SendGrid HTTP API, 5 templates HTML
│   ├── SubmissaoService.java     # Disparo de e-mail nos 3 eventos
│   ├── CoordenadorService.java   # Regra: 1 coordenador por curso
│   ├── RegraAtividadeService.java# Atualiza carga horária do curso
│   ├── PasswordResetService.java # Geração e validação de código
│   └── ...
├── models/
│   ├── Usuario.java
│   ├── Aluno.java
│   ├── Curso.java
│   ├── Turma.java
│   ├── Submissao.java
│   ├── Certificado.java
│   ├── CoordenadorCurso.java
│   ├── RegraAtividade.java
│   ├── AlunoCurso.java
│   └── PasswordResetToken.java
├── dto/                          # DTOs de request/response
├── repository/                   # Interfaces JPA
├── security/                     # JwtAuthenticationFilter, JwtService
└── enums/                        # PerfilUsuario, StatusSubmissao, TurnoTurma
```

---

## ⚙️ Como Executar

### Pré-requisitos
- Java 17+
- Maven
- MySQL 8+

### 1. Clone o repositório

```bash
git clone https://github.com/Jorgefigueredoo/API-Gen-Atvd-Complementares.git
cd API-Gen-Atvd-Complementares
```

### 2. Configure o banco

```sql
CREATE DATABASE api_sistema_senac;
```

### 3. Configure `application-local.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/api_sistema_senac
spring.datasource.username=root
spring.datasource.password=sua_senha

jwt.secret=sua_chave_base64
jwt.expiration=86400000

sendgrid.api.key=${SENDGRID_API_KEY}
sendgrid.from.email=seu@email.com
sendgrid.from.name=Sistema Senac
```

> ⚠️ Nunca suba `application-local.properties` para o repositório. Ele está no `.gitignore`.

### 4. Execute

```bash
./mvnw spring-boot:run
```

Acesse: `http://localhost:8080/swagger-ui/index.html`

---

## 🌐 Deploy (Render + Aiven)

### Variáveis de ambiente no Render

```
SPRING_PROFILES_ACTIVE=prod
MYSQLDATABASE_URL=jdbc:mysql://<host>:<porta>/<banco>?ssl-mode=REQUIRED
MYSQLDATABASE_USER=avnadmin
MYSQLDATABASE_PASSWORD=<senha>
JWT_SECRET=<chave-base64-64-chars>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://progress-hub-six.vercel.app
SENDGRID_API_KEY=<sua-chave>
SENDGRID_FROM_EMAIL=<email-verificado>
```

### Infraestrutura
- **API**: Render (free tier) com limite de memória JVM configurado (`-Xmx400m`)
- **Banco**: Aiven MySQL (free tier, 1GB)
- **E-mail**: SendGrid (free tier, 100 e-mails/dia)
- **Keep-alive**: UptimeRobot pingando a cada 5 minutos

---

## 📌 Observações Técnicas

- `url_arquivo` nos certificados é `LONGTEXT` para suportar base64
- Tabela `tb_historico_status_submissao` criada manualmente (limitação do `ddl-auto=update` com `@ElementCollection`)
- E-mails são disparados de forma assíncrona (`@Async`) para não bloquear a resposta da API
- Timezone dos e-mails configurado para `America/Sao_Paulo`

---

## 👥 Autores

| Integrante |
|---|
| Jorge Figueredo |
| Vitor Santos |
| Lucas Vinícius |
| Renan Souza |
| Antonio Vinícius |
| Maria Vitória |

---

<div align="center">
Feito com ❤️ para o SENAC · 2026
</div>
