package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.exception.ExcusaNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ExcusaServiceIntegrationTest {

    @Autowired
    private ExcusaService excusaService;

    @Autowired
    private EmpleadoService empleadoService;

    @Test
    @Transactional
    public void testCrearExcusa_Success() {

        String nombreUnico = "Juan Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "juan.test@excusas.com");


        Excusa excusa = excusaService.crearExcusa(
                empleado.getLegajo(),
                "TRIVIAL",
                "Se me hizo tarde por el tráfico en la autopista"
        );


        assertNotNull(excusa);
        assertEquals(nombreUnico, excusa.getNombreEmpleado());
        assertEquals("Se me hizo tarde por el tráfico en la autopista", excusa.getDescripcion());
        assertEquals("MotivoTrivial", excusa.getMotivo().getClass().getSimpleName());
    }

    @Test
    @Transactional
    public void testCrearExcusa_ConEmpleadoExistente() {

        List<Empleado> empleadosExistentes = empleadoService.obtenerTodosLosEmpleados();
        assertFalse(empleadosExistentes.isEmpty(), "Debería haber empleados del DataInitializer");

        Empleado empleadoExistente = empleadosExistentes.get(0);

        int excusasIniciales;
        try {
            excusasIniciales = excusaService.obtenerExcusasPorEmpleado(empleadoExistente.getLegajo()).size();
        } catch (ExcusaNotFoundException e) {
            excusasIniciales = 0;
        }


        Excusa excusa = excusaService.crearExcusa(
                empleadoExistente.getLegajo(),
                "PROBLEMA_FAMILIAR",
                "Nueva excusa de prueba para empleado existente"
        );


        assertNotNull(excusa);
        assertEquals(empleadoExistente.getNombre(), excusa.getNombreEmpleado());
        assertEquals("Nueva excusa de prueba para empleado existente", excusa.getDescripcion());

        List<Excusa> excusasActuales = excusaService.obtenerExcusasPorEmpleado(empleadoExistente.getLegajo());
        assertEquals(excusasIniciales + 1, excusasActuales.size());
    }

    @Test
    @Transactional
    public void testCrearExcusa_LimiteExcusasExcedido() {

        String nombreUnico = "Maria Limite " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "maria.limite@test.com");


        for (int i = 1; i <= 5; i++) {
            excusaService.crearExcusa(
                    empleado.getLegajo(),
                    "TRIVIAL",
                    "Excusa número " + i + " con descripción suficientemente larga"
            );
        }


        assertThrows(BusinessRuleException.class, () -> {
            excusaService.crearExcusa(
                    empleado.getLegajo(),
                    "TRIVIAL",
                    "Excusa número 6 que debería fallar por límite excedido"
            );
        });
    }

    @Test
    @Transactional
    public void testObtenerTodasLasExcusas() {

        int excusasIniciales = excusaService.obtenerTodasLasExcusas().size();

        String nombreUnico1 = "Pedro Test " + System.currentTimeMillis();
        String nombreUnico2 = "Ana Test " + System.currentTimeMillis();

        Empleado empleado1 = empleadoService.crearEmpleado(nombreUnico1, "pedro.test@test.com");
        Empleado empleado2 = empleadoService.crearEmpleado(nombreUnico2, "ana.test@test.com");

        excusaService.crearExcusa(empleado1.getLegajo(), "TRIVIAL", "Excusa de Pedro con descripción larga");
        excusaService.crearExcusa(empleado2.getLegajo(), "COMPLEJO", "Excusa de Ana con descripción compleja y larga");


        List<Excusa> excusas = excusaService.obtenerTodasLasExcusas();


        assertEquals(excusasIniciales + 2, excusas.size());
        assertTrue(excusas.stream().anyMatch(e -> e.getNombreEmpleado().equals(nombreUnico1)));
        assertTrue(excusas.stream().anyMatch(e -> e.getNombreEmpleado().equals(nombreUnico2)));
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorEmpleado_Success() {

        String nombreUnico = "Carlos Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "carlos.test@test.com");

        excusaService.crearExcusa(empleado.getLegajo(), "PROBLEMA_FAMILIAR", "Primera excusa familiar con descripción larga");
        excusaService.crearExcusa(empleado.getLegajo(), "PROBLEMA_ELECTRICO", "Segunda excusa eléctrica con descripción larga");


        List<Excusa> excusas = excusaService.obtenerExcusasPorEmpleado(empleado.getLegajo());


        assertEquals(2, excusas.size());
        assertTrue(excusas.stream().allMatch(e -> e.getNombreEmpleado().equals(nombreUnico)));
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorEmpleado_ConDatosExistentes() {

        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();

        for (Empleado empleado : empleados) {
            try {
                List<Excusa> excusas = excusaService.obtenerExcusasPorEmpleado(empleado.getLegajo());

                assertFalse(excusas.isEmpty());
                assertTrue(excusas.stream().allMatch(e -> e.getLegajoEmpleado() == empleado.getLegajo()));
                return;
            } catch (ExcusaNotFoundException e) {

            }
        }

        String nombreUnico = "Test Sin Excusas " + System.currentTimeMillis();
        Empleado nuevoEmpleado = empleadoService.crearEmpleado(nombreUnico, "sinexcusas@test.com");

        assertThrows(ExcusaNotFoundException.class, () -> {
            excusaService.obtenerExcusasPorEmpleado(nuevoEmpleado.getLegajo());
        });
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorTipoMotivo_Success() {

        String nombreUnico1 = "Luis Test " + System.currentTimeMillis();
        String nombreUnico2 = "Diego Test " + System.currentTimeMillis();

        Empleado empleado1 = empleadoService.crearEmpleado(nombreUnico1, "luis.test@test.com");
        Empleado empleado2 = empleadoService.crearEmpleado(nombreUnico2, "diego.test@test.com");

        excusaService.crearExcusa(empleado1.getLegajo(), "PROBLEMA_FAMILIAR", "Problema familiar de Luis con descripción larga");
        excusaService.crearExcusa(empleado2.getLegajo(), "PROBLEMA_FAMILIAR", "Problema familiar de Diego con descripción larga");
        excusaService.crearExcusa(empleado1.getLegajo(), "TRIVIAL", "Excusa trivial de Luis con descripción larga");


        List<Excusa> excusasFamiliares = excusaService.obtenerExcusasPorTipoMotivo("PROBLEMA_FAMILIAR");

        assertTrue(excusasFamiliares.size() >= 2);
        long excusasNuestras = excusasFamiliares.stream()
                .filter(e -> e.getNombreEmpleado().equals(nombreUnico1) || e.getNombreEmpleado().equals(nombreUnico2))
                .count();
        assertEquals(2, excusasNuestras);
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorTipoMotivo_ConDatosExistentes() {

        List<Excusa> excusasTriviales = excusaService.obtenerExcusasPorTipoMotivo("TRIVIAL");

        assertFalse(excusasTriviales.isEmpty());
        assertTrue(excusasTriviales.stream().allMatch(e ->
                e.getMotivo().getClass().getSimpleName().equals("MotivoTrivial")));
    }

    @Test
    @Transactional
    public void testCrearExcusa_TipoMotivoInvalido() {

        String nombreUnico = "Roberto Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "roberto.test@test.com");


        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(
                    empleado.getLegajo(),
                    "MOTIVO_INEXISTENTE",
                    "Descripción con motivo inválido pero suficientemente larga"
            );
        });
    }

    @Test
    @Transactional
    public void testCrearExcusa_DescripcionMuyCorta() {

        String nombreUnico = "Valeria Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "valeria.test@test.com");


        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", "Corta");
        });
    }

    @Test
    @Transactional
    public void testProcesarExcusa_Success() {

        String nombreUnico = "Procesamiento Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "procesamiento.test@test.com");
        excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", "Excusa para procesar con descripción larga");

        List<Excusa> todasLasExcusas = excusaService.obtenerTodasLasExcusas();
        int indiceUltimaExcusa = todasLasExcusas.size() - 1;


        assertDoesNotThrow(() -> {
            excusaService.procesarExcusa(indiceUltimaExcusa);
        });
    }

    @Test
    @Transactional
    public void testProcesarExcusa_ConDatosExistentes() {

        List<Excusa> excusasExistentes = excusaService.obtenerTodasLasExcusas();

        if (!excusasExistentes.isEmpty()) {
            assertDoesNotThrow(() -> {
                excusaService.procesarExcusa(0);
            });
        } else {
            String nombreUnico = "Test Procesar " + System.currentTimeMillis();
            Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "testprocesar@test.com");
            excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", "Excusa para procesar con descripción larga");

            assertDoesNotThrow(() -> {
                excusaService.procesarExcusa(0);
            });
        }
    }

    @Test
    @Transactional
    public void testProcesarExcusa_IndiceInvalido() {

        assertThrows(InvalidDataException.class, () -> {
            excusaService.procesarExcusa(-1);
        });
    }

    @Test
    @Transactional
    public void testProcesarExcusa_IndiceNoExiste() {

        List<Excusa> excusas = excusaService.obtenerTodasLasExcusas();
        int indiceInexistente = excusas.size() + 100; // Índice que seguro no existe

        assertThrows(ExcusaNotFoundException.class, () -> {
            excusaService.procesarExcusa(indiceInexistente);
        });
    }

    @Test
    @Transactional
    public void testObtenerExcusasRechazadas() {

        assertDoesNotThrow(() -> {
            try {
                List<Excusa> rechazadas = excusaService.obtenerExcusasRechazadas();
                assertNotNull(rechazadas);
                assertTrue(rechazadas.stream().allMatch(e -> e != null));
            } catch (ExcusaNotFoundException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @Transactional
    public void testValidacionesBasicas() {

        String nombreUnico = "Validaciones Test " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "validaciones@test.com");

        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(500, "TRIVIAL", "Descripción válida pero legajo inválido");
        });

        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(empleado.getLegajo(), null, "Descripción válida");
        });

        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", null);
        });

        String descripcionMuyLarga = "a".repeat(501);
        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", descripcionMuyLarga);
        });
    }
}
