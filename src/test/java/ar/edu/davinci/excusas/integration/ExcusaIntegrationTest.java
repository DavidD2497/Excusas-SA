package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EmpleadoController;
import ar.edu.davinci.excusas.controller.ExcusaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
public class ExcusaIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer legajoEmpleadoTest;

    private String getExcusasUrl() {
        return "http://localhost:" + port + "/api/excusas";
    }

    private String getEmpleadosUrl() {
        return "http://localhost:" + port + "/api/empleados";
    }

    @BeforeEach
    public void setUp() {
        EmpleadoController.EmpleadoRequest empleadoRequest = new EmpleadoController.EmpleadoRequest();
        empleadoRequest.setNombre("Empleado Test");
        empleadoRequest.setEmail("empleado.test@excusas.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(empleadoRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getEmpleadosUrl(), entity, Map.class);
        legajoEmpleadoTest = (Integer) response.getBody().get("legajo");
    }

    @Test
    public void deberiaCrearExcusaTrivial() throws Exception {
        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajoEmpleadoTest);
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Llegué tarde por el tráfico");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getExcusasUrl(), entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> excusa = response.getBody();
        assertThat(excusa.get("nombreEmpleado")).isEqualTo("Empleado Test");
        assertThat(excusa.get("emailEmpleado")).isEqualTo("empleado.test@excusas.com");
        assertThat(excusa.get("legajoEmpleado")).isEqualTo(legajoEmpleadoTest);
        assertThat(excusa.get("descripcion")).isEqualTo("Llegué tarde por el tráfico");
        assertThat(excusa.get("tipoMotivo")).isEqualTo("MotivoTrivial");
    }

    @Test
    public void deberiaCrearExcusaProblemaElectrico() throws Exception {
        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajoEmpleadoTest);
        request.setTipoMotivo("PROBLEMA_ELECTRICO");
        request.setDescripcion("Se cortó la luz en mi barrio");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);


        ResponseEntity<Map> response = restTemplate.postForEntity(getExcusasUrl(), entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> excusa = response.getBody();
        assertThat(excusa.get("tipoMotivo")).isEqualTo("MotivoProblemaElectrico");
        assertThat(excusa.get("descripcion")).isEqualTo("Se cortó la luz en mi barrio");
    }

    @Test
    public void deberiaProcesarExcusa() throws Exception {
        Map<String, Object> excusaCreada = crearExcusa("TRIVIAL", "Excusa para procesar");

        ResponseEntity<String> response = restTemplate.postForEntity(
                getExcusasUrl() + "/0/procesar", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Excusa procesada correctamente");
    }

    @Test
    public void deberiaObtenerTodasLasExcusas() throws Exception {
        crearExcusa("TRIVIAL", "Primera excusa");
        crearExcusa("COMPLEJO", "Segunda excusa");

        ResponseEntity<List> response = restTemplate.getForEntity(getExcusasUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);

        List<Map<String, Object>> excusas = (List<Map<String, Object>>) response.getBody();
        for (Map<String, Object> excusa : excusas) {
            assertThat(excusa.get("nombreEmpleado")).isNotNull();
            assertThat(excusa.get("emailEmpleado")).isNotNull();
            assertThat(excusa.get("legajoEmpleado")).isNotNull();
            assertThat(excusa.get("descripcion")).isNotNull();
            assertThat(excusa.get("tipoMotivo")).isNotNull();
        }
    }

    @Test
    public void deberiaObtenerExcusasPorEmpleado() throws Exception {
        crearExcusa("TRIVIAL", "Excusa 1 del empleado");
        crearExcusa("PROBLEMA_FAMILIAR", "Excusa 2 del empleado");

        ResponseEntity<List> response = restTemplate.getForEntity(
                getExcusasUrl() + "/empleado/" + legajoEmpleadoTest, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);

        List<Map<String, Object>> excusas = (List<Map<String, Object>>) response.getBody();
        for (Map<String, Object> excusa : excusas) {
            assertThat(excusa.get("legajoEmpleado")).isEqualTo(legajoEmpleadoTest);
        }
    }

    @Test
    public void deberiaRechazarTipoMotivoInvalido() throws Exception {
        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajoEmpleadoTest);
        request.setTipoMotivo("MOTIVO_INEXISTENTE");
        request.setDescripcion("Excusa con motivo inválido");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getExcusasUrl(), entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> crearExcusa(String tipoMotivo, String descripcion) {
        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajoEmpleadoTest);
        request.setTipoMotivo(tipoMotivo);
        request.setDescripcion(descripcion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getExcusasUrl(), entity, Map.class);
        return response.getBody();
    }
}
