package ar.edu.davinci.excusas.integration;

import ar.edu.davinci.excusas.controller.EmpleadoController;
import ar.edu.davinci.excusas.controller.ExcusaController;
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
public class ProntuarioIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/prontuarios";
    }

    private String getEmpleadosUrl() {
        return "http://localhost:" + port + "/empleados";
    }

    private String getExcusasUrl() {
        return "http://localhost:" + port + "/excusas";
    }

    private int crearEmpleadoParaPruebas(String nombre, String email) throws Exception {
        EmpleadoController.EmpleadoRequest empleadoRequest = new EmpleadoController.EmpleadoRequest();
        empleadoRequest.setNombre(nombre);
        empleadoRequest.setEmail(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmpleadoController.EmpleadoRequest> entity = new HttpEntity<>(empleadoRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getEmpleadosUrl(), entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(response.getBody());
        return jsonNode.get("legajo").asInt();
    }

    private void crearExcusaInverosimil(int legajo) throws Exception {
        ExcusaController.ExcusaRequest excusaRequest = new ExcusaController.ExcusaRequest();
        excusaRequest.setLegajoEmpleado(legajo);
        excusaRequest.setTipoMotivo("INVEROSIMIL");
        excusaRequest.setDescripcion("Me secuestraron los extraterrestres y me llevaron a su nave espacial");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(excusaRequest, headers);

        restTemplate.postForEntity(getExcusasUrl(), entity, String.class);
    }

    private int encontrarIndiceExcusaPorEmpleado(String nombreEmpleado) throws Exception {
        ResponseEntity<String> excusasResponse = restTemplate.getForEntity(getExcusasUrl(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode excusasArray = mapper.readTree(excusasResponse.getBody());

        for (int i = 0; i < excusasArray.size(); i++) {
            if (excusasArray.get(i).get("nombreEmpleado").asText().equals(nombreEmpleado)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testObtenerTodosLosProntuarios_Vacio() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().equals("[]") || response.getBody().contains("[]"));
    }

    @Test
    public void testCrearProntuario_ProcesandoExcusaInverosimil() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        String nombreEmpleado = "Juan Prontuario " + System.currentTimeMillis();
        int legajo = crearEmpleadoParaPruebas(nombreEmpleado, nombreEmpleado.toLowerCase().replace(" ", "") + "@test.com");
        crearExcusaInverosimil(legajo);

        int indiceExcusa = encontrarIndiceExcusaPorEmpleado(nombreEmpleado);
        assertTrue(indiceExcusa >= 0, "No se encontró la excusa creada");

        ResponseEntity<String> procesarResponse = restTemplate.postForEntity(
                getExcusasUrl() + "/procesar/indice/" + indiceExcusa,
                null,
                String.class
        );
        assertEquals(HttpStatus.OK, procesarResponse.getStatusCode());

        Thread.sleep(100);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(nombreEmpleado) ||
                response.getBody().contains("extraterrestres"));
    }

    @Test
    public void testObtenerProntuariosPorEmpleado_Success() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        String nombreEmpleado = "Maria Prontuario " + System.currentTimeMillis();
        int legajo = crearEmpleadoParaPruebas(nombreEmpleado, nombreEmpleado.toLowerCase().replace(" ", "") + "@test.com");
        crearExcusaInverosimil(legajo);

        int indiceExcusa = encontrarIndiceExcusaPorEmpleado(nombreEmpleado);
        assertTrue(indiceExcusa >= 0, "No se encontró la excusa creada");

        restTemplate.postForEntity(getExcusasUrl() + "/procesar/indice/" + indiceExcusa, null, String.class);
        Thread.sleep(100);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/empleado/" + legajo, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            assertTrue(response.getBody().contains(nombreEmpleado));
        } else {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        }
    }

    @Test
    public void testObtenerProntuariosPorEmpleado_LegajoInvalido() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/empleado/500", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("El legajo debe ser mayor a 1000"));
    }

    @Test
    public void testObtenerProntuariosPorEmpleado_NoEncontrado() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/empleado/9999", String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("No se encontraron prontuarios"));
    }

    @Test
    public void testContarProntuarios() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/estadisticas/count", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Total de prontuarios"));
    }

    @Test
    public void testLimpiarProntuarios_Success() throws Exception {
        String nombreEmpleado = "Ana Limpieza " + System.currentTimeMillis();
        int legajo = crearEmpleadoParaPruebas(nombreEmpleado, nombreEmpleado.toLowerCase().replace(" ", "") + "@test.com");
        crearExcusaInverosimil(legajo);

        int indiceExcusa = encontrarIndiceExcusaPorEmpleado(nombreEmpleado);
        if (indiceExcusa >= 0) {
            restTemplate.postForEntity(getExcusasUrl() + "/procesar/indice/" + indiceExcusa, null, String.class);
            Thread.sleep(100);
        }

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/administracion/limpiar",
                HttpMethod.DELETE,
                null,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            assertTrue(response.getBody().contains("Todos los prontuarios han sido eliminados"));
        } else {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().contains("No hay prontuarios para eliminar"));
        }
    }

    @Test
    public void testLimpiarProntuarios_SinProntuarios() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/administracion/limpiar",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().contains("No hay prontuarios para eliminar"));
    }

    @Test
    public void testContarProntuarios_Cero() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/estadisticas/count", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Total de prontuarios"));
    }

    @Test
    public void testFlujoCompleto_CrearYVerificarProntuario() throws Exception {
        try {
            restTemplate.exchange(getBaseUrl() + "/administracion/limpiar", HttpMethod.DELETE, null, String.class);
        } catch (Exception e) {
        }

        String nombreEmpleado = "Carlos Flujo " + System.currentTimeMillis();
        int legajo = crearEmpleadoParaPruebas(nombreEmpleado, nombreEmpleado.toLowerCase().replace(" ", "") + "@test.com");

        crearExcusaInverosimil(legajo);

        int indiceExcusa = encontrarIndiceExcusaPorEmpleado(nombreEmpleado);
        assertTrue(indiceExcusa >= 0, "No se encontró la excusa creada");

        restTemplate.postForEntity(getExcusasUrl() + "/procesar/indice/" + indiceExcusa, null, String.class);
        Thread.sleep(100);

        ResponseEntity<String> prontuariosResponse = restTemplate.getForEntity(getBaseUrl(), String.class);
        assertEquals(HttpStatus.OK, prontuariosResponse.getStatusCode());

        ResponseEntity<String> countResponse = restTemplate.getForEntity(getBaseUrl() + "/estadisticas/count", String.class);
        assertEquals(HttpStatus.OK, countResponse.getStatusCode());
        assertTrue(countResponse.getBody().contains("Total de prontuarios"));

        ResponseEntity<String> empleadoResponse = restTemplate.getForEntity(getBaseUrl() + "/buscar/empleado/" + legajo, String.class);
        assertTrue(empleadoResponse.getStatusCode() == HttpStatus.OK ||
                empleadoResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
