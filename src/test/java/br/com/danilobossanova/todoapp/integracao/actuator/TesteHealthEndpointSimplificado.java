package br.com.danilobossanova.todoapp.integracao.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Versão simplificada dos testes para diagnosticar o problema do ApplicationContext.
 *
 * DIAGNÓSTICO: Vamos testar apenas um endpoint por vez para identificar a causa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TesteHealthEndpointSimplificado {

    @LocalServerPort
    private int porta;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    @DisplayName("Teste básico - Context deve carregar corretamente")
    void contextCarregaCorretamente() {
        // Este teste verifica se o contexto Spring carrega sem problemas
        assertThat(porta).isGreaterThan(0);
    }

    @Test
    @DisplayName("Teste básico - Health endpoint deve responder")
    void healthEndpointDeveResponder() {
        // Given
        String url = String.format("http://localhost:%d/actuator/health", porta);

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        System.out.println("Status Code: " + resposta.getStatusCode());
        System.out.println("Response Body: " + resposta.getBody());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}