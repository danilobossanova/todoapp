package br.com.danilobossanova.todoapp.integracao.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de diagnóstico para investigar por que o health endpoint está retornando 503.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TesteDiagnosticoHealth {

    @LocalServerPort
    private int porta;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    @DisplayName("DIAGNÓSTICO - Investigar resposta completa do health endpoint")
    void investigarRespostaHealthEndpoint() {
        // Given
        String url = String.format("http://localhost:%d/actuator/health", porta);

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then - IMPRIMIR TUDO PARA DIAGNÓSTICO
        System.out.println("=== DIAGNÓSTICO HEALTH ENDPOINT ===");
        System.out.println("URL: " + url);
        System.out.println("Status Code: " + resposta.getStatusCode());
        System.out.println("Headers: " + resposta.getHeaders());
        System.out.println("Body: " + resposta.getBody());
        System.out.println("=====================================");

        // Não faz assertions - apenas imprime para análise
    }
}