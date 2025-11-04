# TodoApp

Aplicação de gerenciamento de tarefas desenvolvida em **Java 17** com **Spring Boot**, seguindo princípios de **Clean Code**, **arquitetura em camadas** e **boas práticas de engenharia de software**.
Fornece uma **API RESTful** robusta para criação, atualização, listagem e exclusão de tarefas, com suporte a diferentes **prioridades** e **status**.

---

## 🧩 Arquitetura e Organização

A estrutura do projeto foi desenhada para garantir **manutenibilidade, escalabilidade e clareza**:

* **Controller** → Camada responsável por expor os endpoints REST e tratar requisições HTTP.
* **DTOs (Data Transfer Objects)** → Objetos para transporte de dados, divididos em `dto/request` e `dto/response`.
* **Entity** → Modelagem das entidades do domínio, como `Task`.
* **Repository** → Interfaces de acesso ao banco de dados, utilizando **Spring Data JPA**.
* **Service** → Contém as regras de negócio, com implementações em `service/impl`.
* **Specification** → Filtros dinâmicos para consultas personalizadas.
* **Exception** → Tratamento centralizado de exceções, garantindo respostas padronizadas.

---

## 🐳 Execução com Docker (Recomendado)

> ✅ **Não é necessário ter Java ou Maven instalados** para executar a aplicação com Docker.

### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/danilobossanova/todoapp.git
cd todoapp
```

### 2️⃣ Construir a imagem Docker

```bash
docker build -t todoapp:springboot .
```

### 3️⃣ Executar o container

```bash
docker run -p 8080:8080 todoapp:springboot
```

### 4️⃣ Acessar a aplicação

* API: [http://localhost:8080](http://localhost:8080)
* Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🩺 Monitoramento e Saúde da Aplicação

A aplicação expõe endpoints de **monitoramento e métricas** via **Spring Actuator**, ideais para integração com **Prometheus**, **Grafana** e **Kubernetes**.

| Endpoint               | Método | Descrição                                                     |
| ---------------------- | ------ | ------------------------------------------------------------- |
| `/actuator/health`     | `GET`  | Verifica o status geral da aplicação (`UP`/`DOWN`).           |
| `/actuator/info`       | `GET`  | Retorna informações estáticas (nome, versão, ambiente).       |
| `/actuator/metrics`    | `GET`  | Lista métricas da JVM, requisições HTTP e outros indicadores. |
| `/actuator/prometheus` | `GET`  | Expõe métricas no formato compatível com Prometheus.          |

**Exemplo de uso:**

```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**

```json
{ "status": "UP" }
```

---

## 📘 Swagger — Documentação Interativa da API

A documentação interativa da API está disponível após a inicialização do projeto:

* [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)

Permite explorar e testar os endpoints diretamente pelo navegador.

---

## 🧪 Testes Automatizados

O projeto inclui **testes unitários e de integração** para garantir qualidade e estabilidade.
Os testes estão em `src/test/java`.

### Execução Local

**Linux/macOS**

```bash
./mvnw test
```

**Windows**

```bash
mvnw.cmd test
```

### Execução via Docker

O `Dockerfile` utiliza **multi-stage build**:
a etapa de **build** executa todos os testes antes de gerar a imagem final.
Se algum teste falhar, o build será interrompido automaticamente.

Para testar a aplicação em execução:

```bash
docker run -d -p 8080:8080 --name todoapp todoapp:springboot
curl http://localhost:8080/actuator/health
```

---

## 🔗 Endpoints da API

A API principal está acessível em `/api/v1/tasks`.

### CRUD de Tarefas

| Método   | Endpoint             | Descrição                      |
| -------- | -------------------- | ------------------------------ |
| `POST`   | `/api/v1/tasks`      | Cria uma nova tarefa.          |
| `GET`    | `/api/v1/tasks`      | Lista todas as tarefas.        |
| `GET`    | `/api/v1/tasks/{id}` | Retorna uma tarefa específica. |
| `PUT`    | `/api/v1/tasks/{id}` | Atualiza uma tarefa existente. |
| `DELETE` | `/api/v1/tasks/{id}` | Remove uma tarefa pelo ID.     |

### Atualização de Status

| Método  | Endpoint                      | Descrição                            |
| ------- | ----------------------------- | ------------------------------------ |
| `PATCH` | `/api/v1/tasks/{id}/concluir` | Marca uma tarefa como **concluída**. |
| `PATCH` | `/api/v1/tasks/{id}/reabrir`  | Reabre uma tarefa concluída.         |
| `PATCH` | `/api/v1/tasks/{id}/pendente` | Define a tarefa como **pendente**.   |

---

## ⚙️ Configurações

* Arquivo de propriedades: `src/main/resources/application.properties`
* Porta padrão: `8080` (pode ser alterada via propriedade `server.port`)
* Banco de dados configurável para diferentes ambientes (ex.: H2, PostgreSQL, MySQL).

---

## 🤝 Contribuições

Contribuições são bem-vindas!

---

**Autor:** [Danilo Bossanova](https://github.com/danilobossanova)
**Licença:** MIT License
