package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EmpleadoController;
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
public class EmpleadoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/empleados";
    }

    @Test
    public void testCrearEmpleado_Success() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Juan Perez");
        request.setEmail("juan.perez@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Juan Perez"));
        assertTrue(response.getBody().contains("juan.perez@test.com"));
    }

    @Test
    public void testCrearEmpleado_EmailDuplicado() throws Exception {

        EmpleadoController.EmpleadoRequest request1 = new EmpleadoController.EmpleadoRequest();
        request1.setNombre("Maria Garcia");
        request1.setEmail("maria@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity1 = new HttpEntity<>(request1, headers);

        restTemplate.postForEntity(getBaseUrl(), entity1, String.class);

        EmpleadoController.EmpleadoRequest request2 = new EmpleadoController.EmpleadoRequest();
        request2.setNombre("Pedro Lopez");
        request2.setEmail("maria@test.com");

        HttpEntity<EmpleadoController.EmpleadoRequest> entity2 = new HttpEntity<>(request2, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity2, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Ya existe un empleado con el email"));
    }

    @Test
    public void testCrearEmpleado_DatosInvalidos() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("");
        request.setEmail("email-invalido");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testObtenerTodosLosEmpleados() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Ana Martinez");
        request.setEmail("ana@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Ana Martinez"));
    }

    @Test
    public void testObtenerEmpleadoPorLegajo_Success() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Carlos Rodriguez");
        request.setEmail("carlos.rodriguez@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(createResponse.getBody());
        int legajo = jsonNode.get("legajo").asInt();

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/legajo/" + legajo, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Carlos Rodriguez"));
    }

    @Test
    public void testObtenerEmpleadoPorLegajo_NotFound() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/legajo/9999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testObtenerEmpleadoPorLegajo_LegajoInvalido() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/legajo/500", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("El legajo debe ser mayor a 1000"));
    }

    @Test
    public void testEliminarEmpleado_Success() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Luis Fernandez");
        request.setEmail("luis.fernandez@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(createResponse.getBody());
        int legajo = jsonNode.get("legajo").asInt();

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/legajo/" + legajo,
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("eliminado correctamente"));
    }

    @Test
    public void testBuscarPorNombre_Success() throws Exception {

        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Sofia Gonzalez");
        request.setEmail("sofia@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/nombre/Sofia", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Sofia Gonzalez"));
    }

    @Test
    public void testBuscarPorNombre_NotFound() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/nombre/NoExiste", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testBuscarPorNombre_NombreMuyCorto() throws Exception {

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/nombre/A", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("debe tener al menos 2 caracteres"));
    }
}
