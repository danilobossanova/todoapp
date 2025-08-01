package br.com.danilobossanova.todoapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação TodoApp
 *
 * Esta aplicação implementa uma API REST para gerenciamento de tarefas (To-Do List)
 * utilizando Spring Boot, JPA/Hibernate e documentação automática com OpenAPI/Swagger.
 *
 * Padrões de projeto aplicados:
 * - Repository Pattern: Para abstração da camada de dados
 * - DTO Pattern: Para transferência de dados entre camadas
 * - Builder Pattern: Para construção de objetos complexos
 * - Exception Handler Pattern: Para tratamento centralizado de exceções
 *
 * @author Danilo Bossanova
 * @version 1.0
 * @since 2025-07-31
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "TodoApp API",
                version = "1.0.0",
                description = "API REST para gerenciamento de tarefas (To-Do List) com operações CRUD completas",
                contact = @Contact(
                        name = "Danilo Bossanova",
                        email = "danilo@exemplo.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Servidor de Desenvolvimento"
                )
        }
)
public class TodoappApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoappApplication.class, args);
    }

}
