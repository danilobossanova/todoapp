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
 * Testes de integração para endpoints do Actuator
 *
 * CORREÇÕES APLICADAS:
 * - Removidas configurações problemáticas de health groups
 * - Simplificados os testes para evitar conflitos
 * - Focado apenas nos endpoints essenciais
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TesteHealthEndpoint {

    @LocalServerPort
    private int porta;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    @DisplayName("Context deve carregar corretamente")
    void contextCarregaCorretamente() {
        // Teste básico para verificar se o contexto Spring carrega
        assertThat(porta).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve retornar status UP para endpoint health principal")
    void deveRetornarStatusUpParaHealthPrincipal() {
        // Given
        String url = criarUrlActuator("/health");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"status\":\"UP\"");
        assertThat(resposta.getHeaders().getContentType().toString())
                .contains("application/json");
    }

    @Test
    @DisplayName("Deve incluir health indicator customizado 'aplicacao'")
    void deveIncluirHealthIndicatorCustomizado() {
        // Given
        String url = criarUrlActuator("/health");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        String corpo = resposta.getBody();
        System.out.println("Health Response: " + corpo); // Debug

        assertThat(corpo).contains("\"components\":");
        assertThat(corpo).contains("\"aplicacao\":"); // Nosso indicator customizado
    }

    @Test
    @DisplayName("Deve incluir health indicators padrão do Spring Boot")
    void deveIncluirHealthIndicatorsPadrao() {
        // Given
        String url = criarUrlActuator("/health");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        String corpo = resposta.getBody();
        // Verifica indicators que devem estar habilitados
        assertThat(corpo).contains("\"db\":"); // Database health indicator
        assertThat(corpo).contains("\"diskSpace\":"); // Disk space health indicator
    }

    @Test
    @DisplayName("Deve expor endpoint info básico")
    void deveExporEndpointInfoBasico() {
        // Given
        String url = criarUrlActuator("/info");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        // Em testes, o info pode estar vazio - isso é normal
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Deve rejeitar endpoints não expostos")
    void deveRejeitarEndpointsNaoExpostos() {
        // Given - tentando acessar endpoint não exposto
        String url = criarUrlActuator("/env");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then - deve retornar erro (404 ou 500) pois endpoint não está exposto
        assertThat(resposta.getStatusCode().is4xxClientError() || resposta.getStatusCode().is5xxServerError())
                .isTrue();

        // Verifica especificamente se não é 200 OK
        assertThat(resposta.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Endpoint discovery deve estar disponível")
    void endpointDiscoveryDeveEstarDisponivel() {
        // Given
        String url = criarUrlActuator("");

        // When
        ResponseEntity<String> resposta = restTemplate.getForEntity(url, String.class);

        // Then
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"_links\":");
    }

    /**
     * Helper method para criar URLs do Actuator
     */
    private String criarUrlActuator(String endpoint) {
        return String.format("http://localhost:%d/actuator%s", porta, endpoint);
    }
}