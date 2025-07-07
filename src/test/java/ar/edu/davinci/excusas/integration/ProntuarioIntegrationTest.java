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
public class ProntuarioIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer legajoEmpleadoTest;

    private String getProntuariosUrl() {
        return "http://localhost:" + port + "/api/prontuarios";
    }

    private String getExcusasUrl() {
        return "http://localhost:" + port + "/api/excusas";
    }

    private String getEmpleadosUrl() {
        return "http://localhost:" + port + "/api/empleados";
    }

    @BeforeEach
    public void setUp() {
        restTemplate.delete(getProntuariosUrl());

        EmpleadoController.EmpleadoRequest empleadoRequest = new EmpleadoController.EmpleadoRequest();
        empleadoRequest.setNombre("Empleado Prontuario Test");
        empleadoRequest.setEmail("empleado.prontuario@excusas.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(empleadoRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(getEmpleadosUrl(), entity, Map.class);
        legajoEmpleadoTest = (Integer) response.getBody().get("legajo");
    }

    @Test
    public void deberiaObtenerProntuariosVacios() throws Exception {
        ResponseEntity<List> response = restTemplate.getForEntity(getProntuariosUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void deberiaCrearProntuarioCuandoSeProcesaExcusaInverosimil() throws Exception {
        crearYProcesarExcusa("INVEROSIMIL", "Me secuestraron los aliens");

        ResponseEntity<List> response = restTemplate.getForEntity(getProntuariosUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);

        List<Map<String, Object>> prontuarios = (List<Map<String, Object>>) response.getBody();
        Map<String, Object> prontuario = prontuarios.get(0);

        assertThat(prontuario.get("nombreEmpleado")).isEqualTo("Empleado Prontuario Test");
        assertThat(prontuario.get("emailEmpleado")).isEqualTo("empleado.prontuario@excusas.com");
        assertThat(prontuario.get("legajo")).isEqualTo(legajoEmpleadoTest);
        assertThat(prontuario.get("descripcionExcusa")).isEqualTo("Me secuestraron los aliens");
        assertThat(prontuario.get("tipoMotivoExcusa")).isEqualTo("MotivoInverosimil");
    }

    @Test
    public void noDeberiaCrearProntuarioParaExcusasTriviales() throws Exception {
        crearYProcesarExcusa("TRIVIAL", "Llegué tarde por el tráfico");

        ResponseEntity<List> response = restTemplate.getForEntity(getProntuariosUrl(), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void deberiaObtenerProntuariosPorEmpleado() throws Exception {
        crearYProcesarExcusa("INVEROSIMIL", "Primera excusa inverosímil");
        crearYProcesarExcusa("INVEROSIMIL", "Segunda excusa inverosímil");

        ResponseEntity<List> response = restTemplate.getForEntity(
                getProntuariosUrl() + "/empleado/" + legajoEmpleadoTest, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);

        List<Map<String, Object>> prontuarios = (List<Map<String, Object>>) response.getBody();
        for (Map<String, Object> prontuario : prontuarios) {
            assertThat(prontuario.get("legajo")).isEqualTo(legajoEmpleadoTest);
            assertThat(prontuario.get("tipoMotivoExcusa")).isEqualTo("MotivoInverosimil");
        }
    }

    @Test
    public void deberiaContarProntuarios() throws Exception {
        crearYProcesarExcusa("INVEROSIMIL", "Excusa 1");
        crearYProcesarExcusa("INVEROSIMIL", "Excusa 2");
        crearYProcesarExcusa("INVEROSIMIL", "Excusa 3");

        ResponseEntity<Integer> response = restTemplate.getForEntity(
                getProntuariosUrl() + "/count", Integer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(3);
    }

    @Test
    public void deberiaLimpiarProntuarios() throws Exception {
        crearYProcesarExcusa("INVEROSIMIL", "Excusa para limpiar");

        ResponseEntity<Integer> countResponse = restTemplate.getForEntity(
                getProntuariosUrl() + "/count", Integer.class);
        assertThat(countResponse.getBody()).isGreaterThan(0);

        ResponseEntity<String> response = restTemplate.exchange(
                getProntuariosUrl(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Todos los prontuarios han sido eliminados");

        ResponseEntity<Integer> countAfterResponse = restTemplate.getForEntity(
                getProntuariosUrl() + "/count", Integer.class);
        assertThat(countAfterResponse.getBody()).isEqualTo(0);
    }

    private void crearYProcesarExcusa(String tipoMotivo, String descripcion) {
        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajoEmpleadoTest);
        request.setTipoMotivo(tipoMotivo);
        request.setDescripcion(descripcion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getExcusasUrl(), entity, Map.class);

        ResponseEntity<List> excusasResponse = restTemplate.getForEntity(getExcusasUrl(), List.class);
        int ultimoIndice = excusasResponse.getBody().size() - 1;

        restTemplate.postForEntity(
                getExcusasUrl() + "/" + ultimoIndice + "/procesar", null, String.class);
    }
}
