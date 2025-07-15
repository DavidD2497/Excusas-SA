package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EncargadoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EncargadoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/encargados";
    }

    @Test
    public void testObtenerTodosLosEncargados() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("recepcionista"));
        assertTrue(response.getBody().contains("supervisor"));
        assertTrue(response.getBody().contains("gerente"));
        assertTrue(response.getBody().contains("ceo"));
    }

    @Test
    public void testObtenerEncargado_Success() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/tipo/recepcionista", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("recepcionista"));
        assertTrue(response.getBody().contains("laura@excusas.com"));
    }

    @Test
    public void testObtenerEncargado_TipoInvalido() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/tipo/invalido", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Tipo de encargado no válido"));
    }

    @Test
    public void testCambiarModo_Success() throws Exception {

        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("PRODUCTIVO");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/tipo/recepcionista/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Modo cambiado a PRODUCTIVO"));
        assertTrue(response.getBody().contains("recepcionista"));
    }

    @Test
    public void testCambiarModo_ModoInvalido() throws Exception {

        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("MODO_INEXISTENTE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/tipo/supervisor/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Modo no válido"));
    }

    @Test
    public void testCambiarModo_TipoEncargadoInvalido() throws Exception {

        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("NORMAL");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/tipo/inexistente/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Tipo de encargado no válido"));
    }

    @Test
    public void testObtenerEncargadosPorCapacidad_Success() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/capacidad/TRIVIAL", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("recepcionista"));
    }

    @Test
    public void testObtenerEncargadosPorCapacidad_CapacidadInvalida() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/capacidad/INEXISTENTE", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Capacidad no válida"));
    }

    @Test
    public void testCambiarModoVago() throws Exception {

        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("VAGO");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/tipo/gerente/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Modo cambiado a VAGO"));
    }

    @Test
    public void testCambiarModoNormal() throws Exception {

        EncargadoController.ModoRequest request = new EncargadoController.ModoRequest();
        request.setModo("NORMAL");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EncargadoController.ModoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/tipo/ceo/modo",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Modo cambiado a NORMAL"));
    }
}
