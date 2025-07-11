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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExcusaIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/excusas";
    }

    private String getEmpleadosUrl() {
        return "http://localhost:" + port + "/empleados";
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

    @Test
    public void testCrearExcusa_Success() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Juan Test", "juan.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Se me hizo tarde por el tráfico en la autopista");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Juan Test"));
        assertTrue(response.getBody().contains("Se me hizo tarde por el tráfico"));
    }

    @Test
    public void testCrearExcusa_EmpleadoNoExiste() throws Exception {

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(9999); // Legajo que no existe
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Descripción de prueba para empleado inexistente");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Empleado no encontrado"));
    }

    @Test
    public void testCrearExcusa_TipoMotivoInvalido() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Maria Test", "maria.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("MOTIVO_INEXISTENTE");
        request.setDescripcion("Descripción con motivo inválido");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Tipo de motivo no válido"));
    }

    @Test
    public void testCrearExcusa_DescripcionMuyCorta() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Pedro Test", "pedro.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Corta");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCrearExcusa_LegajoInvalido() throws Exception {

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(500);
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Descripción con legajo inválido");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testObtenerTodasLasExcusas() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Ana Test", "ana.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("PROBLEMA_FAMILIAR");
        request.setDescripcion("Tuve que llevar a mi hijo al médico de urgencia");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Ana Test"));
        assertTrue(response.getBody().contains("médico de urgencia"));
    }

    @Test
    public void testProcesarExcusa_Success() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Carlos Test", "carlos.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("TRIVIAL");
        request.setDescripcion("Me quedé dormido porque no sonó el despertador");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> excusasResponse = restTemplate.getForEntity(getBaseUrl(), String.class);
        String excusasBody = excusasResponse.getBody();

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode excusasArray = mapper.readTree(excusasBody);
        int totalExcusas = excusasArray.size();
        int indiceUltimaExcusa = totalExcusas - 1; // La última excusa creada

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/procesar/indice/" + indiceUltimaExcusa,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Excusa procesada correctamente"));

        assertTrue(response.getBody().contains("Carlos Test") ||
                response.getBody().contains("indice\":" + indiceUltimaExcusa));
    }

    @Test
    public void testProcesarExcusa_IndiceInvalido() throws Exception {

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/procesar/indice/-1", null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("El índice no puede ser negativo"));
    }

    @Test
    public void testProcesarExcusa_IndiceNoExiste() throws Exception {

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/procesar/indice/999", null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Excusa no encontrada"));
    }

    @Test
    public void testObtenerExcusasPorEmpleado_Success() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Sofia Test", "sofia.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("PROBLEMA_ELECTRICO");
        request.setDescripcion("Se cortó la luz en todo el barrio y no pude trabajar desde casa");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/empleado/" + legajo, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Sofia Test"));
        assertTrue(response.getBody().contains("cortó la luz"));
    }

    @Test
    public void testObtenerExcusasPorTipoMotivo_Success() throws Exception {

        int legajo = crearEmpleadoParaPruebas("Luis Test", "luis.test@excusas.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("COMPLEJO");
        request.setDescripcion("Tuve un problema muy complejo que requiere explicación detallada");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/buscar/motivo/COMPLEJO", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Luis Test"));
        assertTrue(response.getBody().contains("problema muy complejo"));
    }

    @Test
    public void testProcesarExcusa_ConEmpleadoEspecifico() throws Exception {

        String nombreUnico = "EmpleadoProcesar_" + System.currentTimeMillis();
        int legajo = crearEmpleadoParaPruebas(nombreUnico, nombreUnico.toLowerCase() + "@test.com");

        ExcusaController.ExcusaRequest request = new ExcusaController.ExcusaRequest();
        request.setLegajoEmpleado(legajo);
        request.setTipoMotivo("PROBLEMA_FAMILIAR");
        request.setDescripcion("Tuve que llevar a mi familiar al hospital de emergencia");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExcusaController.ExcusaRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(getBaseUrl(), entity, String.class);

        ResponseEntity<String> excusasResponse = restTemplate.getForEntity(getBaseUrl(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode excusasArray = mapper.readTree(excusasResponse.getBody());

        int indiceExcusa = -1;
        for (int i = 0; i < excusasArray.size(); i++) {
            if (excusasArray.get(i).get("nombreEmpleado").asText().equals(nombreUnico)) {
                indiceExcusa = i;
                break;
            }
        }

        assertTrue(indiceExcusa >= 0, "No se encontró la excusa creada");

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/procesar/indice/" + indiceExcusa,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Excusa procesada correctamente"));
        assertTrue(response.getBody().contains(nombreUnico));
    }
}
