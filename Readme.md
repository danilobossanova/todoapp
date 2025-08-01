# TodoApp

Este projeto é uma aplicação de gerenciamento de tarefas (To-Do) desenvolvida em Java utilizando o framework Spring Boot. O objetivo é fornecer uma API robusta para criação, atualização, listagem e remoção de tarefas, com suporte a diferentes prioridades e status.

## Arquitetura e Organização

O projeto segue uma arquitetura baseada em camadas, promovendo separação de responsabilidades e facilidade de manutenção:

- **Controller**: Responsável por receber as requisições HTTP e retornar as respostas apropriadas .
- **DTOs**: Objetos de transferência de dados para requests e responses, localizados em `dto/request` e `dto/response`.
- **Entity**: Representa as entidades do domínio, como `Task`.
- **Repository**: Interface para acesso ao banco de dados, utilizando Spring Data JPA.
- **Service**: Camada de regras de negócio, com implementação em `service/impl`.
- **Specification**: Implementa filtros dinâmicos para consultas.
- **Exception**: Gerenciamento centralizado de exceções.

## Swagger (Documentação da API)

A aplicação conta com integração ao Swagger, permitindo a visualização e teste dos endpoints de forma interativa. Para acessar a documentação, execute o projeto e acesse:

```
http://localhost:8080/swagger-ui.html
```

ou

```
http://localhost:8080/swagger-ui/
```

## Testes

Os testes automatizados estão localizados em `src/test/java`. Para executá-los, utilize o Maven:

```
./mvnw test
```

ou, no Windows:

```
mvnw.cmd test
```

## Como Executar o Projeto

1. **Pré-requisitos:**
   - Java 17+
   - Maven 3.8+

2. **Executando com Maven Wrapper:**

   No Linux/macOS:
   ```
   ./mvnw spring-boot:run
   ```
   No Windows:
   ```
   mvnw.cmd spring-boot:run
   ```

3. **Acessando a aplicação:**
   - A API estará disponível em: `http://localhost:8080`
   - Acesse o Swagger para explorar os endpoints.

## Como Executar com Docker

1. Gere o JAR do projeto (caso ainda não tenha):
   ```
   ./mvnw clean package
   ```
   ou no Windows:
   ```
   mvnw.cmd clean package
   ```

2. Construa a imagem Docker:
   ```
   docker build -t todoapp .
   ```

3. Execute o container:
   ```
   docker run -p 8080:8080 todoapp
   ```

A aplicação estará disponível em: http://localhost:8080

## Observações

- O projeto utiliza boas práticas de desenvolvimento, como tratamento global de exceções e uso de DTOs para comunicação.
- O banco de dados pode ser configurado via `src/main/resources/application.properties`.

---

Sinta-se à vontade para contribuir ou sugerir melhorias!
