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


## 🐳 Como Executar com Docker (Recomendado)

> ✅ **Você não precisa ter Java ou Maven instalados** para testar a aplicação usando esta abordagem.

### 1. Clone o projeto e acesse a pasta
```bash
git clone https://github.com/danilobossanova/todoapp.git
cd todoapp
```

### 2. Construa a imagem Docker
```bash
docker build -t todoapp:springboot .
```

### 3. Execute o container
```bash
docker run -p 8080:8080 todoapp:springboot
```

### 4. Acesse a aplicação
- API: [http://localhost:8080](http://localhost:8080)
- Swagger (documentação):  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 5. (Opcional) Verifique se está funcionando

#### Monitoramento e Saúde da Aplicação

Essaaplicação expõe um conjunto de endpoints de monitoramento para verificar seu status e coletar métricas. Esses 
endpoints são úteis para sistemas de monitoramento e para a orquestração de contêineres (como Kubernetes).

-----

#### **Endpoints Principais**

A porta do servidor pode ser configurada no arquivo `application.properties`. Por padrão, é `8080`.

* **Status de Saúde Geral**
  * **Endpoint:** `GET /actuator/health`
  * **Descrição:** Fornece um resumo do status de saúde geral da aplicação e seus componentes (banco de dados, espaço em disco, etc.). A resposta esperada é um status `UP` se tudo estiver funcionando, ou `DOWN` se houver algum problema.
  * **Exemplo de uso:**
    ```
    curl http://localhost:8080/actuator/health
    ```
  * **Exemplo de Resposta:**
    ```
    {
      "status": "UP"
    }
    ```
* **Informações da Aplicação**
  * **Endpoint:** `GET /actuator/info`
  * **Descrição:** Retorna informações estáticas sobre a aplicação, como o nome, versão e ambiente.
  * **Exemplo de uso:**
    ```
    curl http://localhost:8080/actuator/info
    ```
* **Métricas da Aplicação**
  * **Endpoint:** `GET /actuator/metrics`
  * **Descrição:** Lista todas as métricas disponíveis, como uso de memória (`jvm.memory.used`) e requisições HTTP (`http.server.requests`). Você pode obter detalhes de uma métrica específica adicionando seu nome ao endpoint, por exemplo: `/actuator/metrics/jvm.memory.used`.
  * **Exemplo de uso:**
    ```
    curl http://localhost:8080/actuator/metrics
    ```
* **Métricas para Prometheus**
  * **Endpoint:** `GET /actuator/prometheus`
  * **Descrição:** Expõe as métricas da aplicação em um formato compatível com o Prometheus, ideal para ser coletado por ferramentas de monitoramento.
  * **Exemplo de uso:**
    ```
    curl http://localhost:8080/actuator/prometheus
    ```

---

## Swagger (Documentação da API)

A aplicação conta com integração ao Swagger, permitindo a visualização e teste dos endpoints de forma interativa. Para acessar a documentação, execute o projeto e acesse:

```
http://localhost:8080/swagger-ui.html
```

ou

```
http://localhost:8080/swagger-ui/
```


Claro. Aqui está o conteúdo formatado em Markdown, pronto para ser copiado e usado.

-----

## 🧪 Testes Automatizados

Os testes automatizados são uma parte crucial do nosso processo de build, garantindo a qualidade e a estabilidade do código.

Os testes estão localizados em `src/test/java`.

-----

### Execução Local

Para rodar os testes localmente, utilize o wrapper do Maven.

No Linux/macOS:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

-----

### Execução no Docker

Nosso `Dockerfile` é otimizado com um processo de build em duas etapas. A **primeira etapa (`construtor`)** é responsável por rodar todos os testes de unidade e de integração do projeto. O build do Docker falhará se algum teste não passar.

**Você não precisa executar os testes manualmente ao usar o Docker**, pois eles já são verificados automaticamente durante o comando `docker build`.

Para testar a aplicação em um contêiner já em execução, a forma correta é realizar testes de ponta a ponta (end-to-end) via requisições HTTP, pois o contêiner final não inclui o Maven ou o código de teste.

1.  **Inicie o contêiner:**
    ```bash
    docker run -p 8080:8080 -d --name todoapp:ipog todoapp:ipog
    ```
2.  **Verifique a saúde da aplicação** usando o endpoint de saúde exposto:
    ```bash
    curl http://localhost:8080/actuator/health
    ```
3.  **Execute seus testes de API** (como os de Postman ou um pacote de testes de integração) a partir da sua máquina local, apontando para `http://localhost:8080`.

O processo de testes de build não afeta o comando `docker run`, já que ele utiliza a imagem final que não contém mais os arquivos de teste ou o Maven.

---

Com base na imagem dos endpoints da sua aplicação, preparei uma seção completa para o seu README, formatada em Markdown. Esta seção documenta a API REST para gerenciamento de tarefas, explicando cada endpoint, seu método HTTP, a URL, a descrição e o que ele faz.

---

### Endpoints da API REST de Tarefas

A API de Tarefas permite gerenciar tarefas de forma simples e eficiente. A base da URL para todos os endpoints é `/api/v1/tasks`.

---

#### **Endpoints Principais**

**Criar uma nova tarefa**
* **Método:** `POST`
* **URL:** `/api/v1/tasks`
* **Descrição:** Cria uma nova tarefa.

**Listar todas as tarefas**
* **Método:** `GET`
* **URL:** `/api/v1/tasks`
* **Descrição:** Retorna a lista completa de todas as tarefas existentes.

**Buscar uma tarefa por ID**
* **Método:** `GET`
* **URL:** `/api/v1/tasks/{id}`
* **Descrição:** Busca e retorna uma tarefa específica utilizando o seu ID.

**Atualizar uma tarefa**
* **Método:** `PUT`
* **URL:** `/api/v1/tasks/{id}`
* **Descrição:** Atualiza completamente uma tarefa existente com base no seu ID.

**Remover uma tarefa**
* **Método:** `DELETE`
* **URL:** `/api/v1/tasks/{id}`
* **Descrição:** Remove uma tarefa específica com base no seu ID.

---

#### **Endpoints de Atualização de Status**

Esses endpoints são usados para alterar o status de uma tarefa sem a necessidade de um corpo de requisição complexo.

**Concluir uma tarefa**
* **Método:** `PATCH`
* **URL:** `/api/v1/tasks/{id}/concluir`
* **Descrição:** Marca a tarefa como `concluída`.

**Reabrir uma tarefa**
* **Método:** `PATCH`
* **URL:** `/api/v1/tasks/{id}/reabrir`
* **Descrição:** Reabre uma tarefa concluída.

**Marcar como pendente**
* **Método:** `PATCH`
* **URL:** `/api/v1/tasks/{id}/pendente`
* **Descrição:** Marca a tarefa como `pendente`.


## Observações

- O projeto utiliza boas práticas de desenvolvimento, como tratamento global de exceções e uso de DTOs para comunicação.
- O banco de dados pode ser configurado via `src/main/resources/application.properties`.

---

Sinta-se à vontade para contribuir ou sugerir melhorias!
