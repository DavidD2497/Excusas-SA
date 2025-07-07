package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EmpleadoController;
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
public class EmpleadoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/empleados";
    }

    @Test
    public void deberiaCrearEmpleadoYDevolverFormatoCorrecto() throws Exception {
        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre("Juan Pérez");
        request.setEmail("juan.perez@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getBaseUrl(), entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> empleado = response.getBody();
        assertThat(empleado.get("nombre")).isEqualTo("Juan Pérez");
        assertThat(empleado.get("email")).isEqualTo("juan.perez@test.com");
        assertThat(empleado.get("legajo")).isNotNull();
        assertThat((Integer) empleado.get("legajo")).isGreaterThan(1000);
    }

    @Test
    public void deberiaObtenerTodosLosEmpleados() throws Exception {
        crearEmpleado("Ana García", "ana@test.com");
        crearEmpleado("Carlos López", "carlos@test.com");

        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void deberiaObtenerEmpleadoPorLegajo() throws Exception {
        Map<String, Object> empleadoCreado = crearEmpleado("Pedro Martínez", "pedro@test.com");
        Integer legajo = (Integer) empleadoCreado.get("legajo");

        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + legajo, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> empleado = response.getBody();
        assertThat(empleado.get("nombre")).isEqualTo("Pedro Martínez");
        assertThat(empleado.get("email")).isEqualTo("pedro@test.com");
        assertThat(empleado.get("legajo")).isEqualTo(legajo);
    }

    @Test
    public void deberiaBuscarEmpleadosPorNombre() throws Exception {
        crearEmpleado("María González", "maria@test.com");
        crearEmpleado("Mario Rodríguez", "mario@test.com");

        ResponseEntity<List> response = restTemplate.getForEntity(
                getBaseUrl() + "/buscar?nombre=Mar", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void deberiaEliminarEmpleado() throws Exception {
        Map<String, Object> empleadoCreado = crearEmpleado("Empleado Temporal", "temp@test.com");
        Integer legajo = (Integer) empleadoCreado.get("legajo");

        restTemplate.delete(getBaseUrl() + "/" + legajo);

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + legajo, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> crearEmpleado(String nombre, String email) {
        EmpleadoController.EmpleadoRequest request = new EmpleadoController.EmpleadoRequest();
        request.setNombre(nombre);
        request.setEmail(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getBaseUrl(), entity, Map.class);
        return response.getBody();
    }
}
