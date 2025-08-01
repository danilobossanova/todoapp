# ✅ Projeto Spring Boot - API de Cadastro de Tarefas (TODO)

Este projeto é uma API REST desenvolvida com **Spring Boot 3** e **Java 17**, cujo objetivo é gerenciar tarefas 
(TODOs) de forma simples e eficaz. A aplicação oferece endpoints, persistência em memória com **H2**,  documentação 
com **Swagger** e testes automatizados com **JUnit 5**.

---

## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot 3
- Spring Data JPA
- Maven
- Lombok
- SpringDoc OpenAPI
- JUnit 5 + Mockito
- Docker
- Git Flow

---

## 🧠 Decisões Técnicas

- **Arquitetura Limpa**: Separação clara entre camadas `controller`, `service`, `repository`, e `domain`.
- **DTOs e Mapeamento**: Separação entre entidades internas e representação externa da API.
- **Testes Automatizados**: Garantia de integridade nas funcionalidades principais.

---

## 📂 Funcionalidades da API

- 📋 Criar, listar, atualizar e remover tarefas
- 📌 Marcar tarefas como pendentes ou concluídas
- 🔎 Filtros por status, data e prioridade

---

## ✅ Como Executar os Testes

Execute os testes automatizados com o Maven:

```bash
mvn test
