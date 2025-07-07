package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EncargadoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EncargadoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/encargados";
    }

    @Test
    public void deberiaObtenerTodosLosEncargados() throws Exception {
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(4);

        List<Map<String, Object>> encargados = (List<Map<String, Object>>) response.getBody();

        for (Map<String, Object> encargado : encargados) {
            assertThat(encargado.get("tipo")).isNotNull();
            assertThat(encargado.get("emailOrigen")).isNotNull();
            assertThat(encargado.get("modoActual")).isNotNull();
            assertThat(encargado.get("capacidades")).isNotNull();
            assertThat(encargado.get("capacidades")).isInstanceOf(List.class);
        }
    }

    @Test
    public void deberiaObtenerEncargadoEspecifico() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/recepcionista", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> encargado = response.getBody();
        assertThat(encargado.get("tipo")).isEqualTo("recepcionista");
        assertThat(encargado.get("emailOrigen")).isEqualTo("laura@excusas.com");
        assertThat(encargado.get("modoActual")).isEqualTo("ModoNormal");

        List<String> capacidades = (List<String>) encargado.get("capacidades");
        assertThat(capacidades).contains("TRIVIAL");
    }

    @Test
    public void deberiaCambiarModoDeEncargado() throws Exception {
        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("PRODUCTIVO");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/supervisor/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Modo cambiado a PRODUCTIVO para supervisor");

        ResponseEntity<Map> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/supervisor", Map.class);

        Map<String, Object> encargado = getResponse.getBody();
        assertThat(encargado.get("modoActual")).isEqualTo("ModoProductivo");
    }

    @Test
    public void deberiaValidarCapacidadesPorTipoDeEncargado() throws Exception {
        ResponseEntity<Map> recepcionistaResponse = restTemplate.getForEntity(
                getBaseUrl() + "/recepcionista", Map.class);
        Map<String, Object> recepcionista = recepcionistaResponse.getBody();
        List<String> capacidadesRecepcionista = (List<String>) recepcionista.get("capacidades");
        assertThat(capacidadesRecepcionista).containsExactly("TRIVIAL");

        ResponseEntity<Map> supervisorResponse = restTemplate.getForEntity(
                getBaseUrl() + "/supervisor", Map.class);
        Map<String, Object> supervisor = supervisorResponse.getBody();
        List<String> capacidadesSupervisor = (List<String>) supervisor.get("capacidades");
        assertThat(capacidadesSupervisor).containsExactly("MODERADO");

        ResponseEntity<Map> gerenteResponse = restTemplate.getForEntity(
                getBaseUrl() + "/gerente", Map.class);
        Map<String, Object> gerente = gerenteResponse.getBody();
        List<String> capacidadesGerente = (List<String>) gerente.get("capacidades");
        assertThat(capacidadesGerente).containsExactly("COMPLEJO");

        ResponseEntity<Map> ceoResponse = restTemplate.getForEntity(
                getBaseUrl() + "/ceo", Map.class);
        Map<String, Object> ceo = ceoResponse.getBody();
        List<String> capacidadesCeo = (List<String>) ceo.get("capacidades");
        assertThat(capacidadesCeo).containsExactly("INVEROSIMIL");
    }

    @Test
    public void deberiaRechazarModoInvalido() throws Exception {
        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("MODO_INEXISTENTE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/recepcionista/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
