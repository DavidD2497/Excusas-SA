package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProntuarioServiceIntegrationTest {

    @Autowired
    private ProntuarioService prontuarioService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ExcusaService excusaService;

    @Test
    @Transactional
    public void testObtenerTodosLosProntuarios() {

        List<Prontuario> prontuariosIniciales = prontuarioService.obtenerTodosLosProntuarios();
        int cantidadInicial = prontuariosIniciales.size();

        assertNotNull(prontuariosIniciales);
        assertTrue(cantidadInicial >= 0); // Puede ser 0 o más
        for (Prontuario prontuario : prontuariosIniciales) {
            assertNotNull(prontuario.getEmpleado());
            assertNotNull(prontuario.getExcusa());
            assertTrue(prontuario.getLegajo() > 1000);
        }
    }

    @Test
    @Transactional
    public void testCrearProntuarioViaProcesamiento() {

        int prontuariosIniciales = prontuarioService.obtenerTodosLosProntuarios().size();

        String nombreUnico = "Test Prontuario " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "prontuario@test.com");

        excusaService.crearExcusa(
                empleado.getLegajo(),
                "INVEROSIMIL",
                "Me secuestraron los extraterrestres y me llevaron a su nave espacial"
        );

        List<ar.edu.davinci.excusas.model.excusas.Excusa> todasLasExcusas = excusaService.obtenerTodasLasExcusas();
        int indiceUltimaExcusa = todasLasExcusas.size() - 1;

        excusaService.procesarExcusa(indiceUltimaExcusa);

        List<Prontuario> prontuariosFinales = prontuarioService.obtenerTodosLosProntuarios();
        assertTrue(prontuariosFinales.size() >= prontuariosIniciales);

        boolean encontradoNuestro = prontuariosFinales.stream()
                .anyMatch(p -> p.getEmpleado().getNombre().equals(nombreUnico));

        if (prontuariosFinales.size() > prontuariosIniciales) {
            assertTrue(encontradoNuestro || prontuariosFinales.size() == prontuariosIniciales + 1);
        }
    }

    @Test
    @Transactional
    public void testObtenerProntuariosPorEmpleado_ConDatosExistentes() {

        List<Prontuario> todosProntuarios = prontuarioService.obtenerTodosLosProntuarios();

        if (!todosProntuarios.isEmpty()) {
            Prontuario primerProntuario = todosProntuarios.get(0);
            int legajoEmpleado = primerProntuario.getEmpleado().getLegajo();

            List<Prontuario> prontuariosEmpleado = prontuarioService.obtenerProntuariosPorEmpleado(legajoEmpleado);

            assertFalse(prontuariosEmpleado.isEmpty());
            assertTrue(prontuariosEmpleado.stream()
                    .allMatch(p -> p.getEmpleado().getLegajo() == legajoEmpleado));
        } else {
            String nombreUnico = "Test Empleado Prontuario " + System.currentTimeMillis();
            Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "empleadoprontuario@test.com");

            assertThrows(BusinessRuleException.class, () -> {
                prontuarioService.obtenerProntuariosPorEmpleado(empleado.getLegajo());
            });
        }
    }

    @Test
    @Transactional
    public void testObtenerProntuariosPorEmpleado_LegajoInvalido() {

        assertThrows(InvalidDataException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(500);
        });
    }

    @Test
    @Transactional
    public void testObtenerProntuariosPorEmpleado_NoEncontrado() {
        String nombreUnico = "Sin Prontuarios " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "sinprontuarios@test.com");


        assertThrows(BusinessRuleException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(empleado.getLegajo());
        });
    }

    @Test
    @Transactional
    public void testContarProntuarios() {

        int count = prontuarioService.contarProntuarios();


        assertTrue(count >= 0);
        List<Prontuario> todos = prontuarioService.obtenerTodosLosProntuarios();
        assertEquals(todos.size(), count);
    }

    @Test
    @Transactional
    public void testLimpiarProntuarios() {

        int prontuariosIniciales = prontuarioService.contarProntuarios();

        if (prontuariosIniciales > 0) {
            int cantidadEliminada = prontuarioService.limpiarProntuarios();

            assertEquals(prontuariosIniciales, cantidadEliminada);
            assertEquals(0, prontuarioService.contarProntuarios());
        } else {
            assertThrows(BusinessRuleException.class, () -> {
                prontuarioService.limpiarProntuarios();
            });
        }
    }

    @Test
    @Transactional
    public void testLimpiarProntuarios_DespuesDeCrearUno() {

        try {
            prontuarioService.limpiarProntuarios();
        } catch (BusinessRuleException e) {
        }

        String nombreUnico = "Test Limpiar " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "testlimpiar@test.com");

        excusaService.crearExcusa(
                empleado.getLegajo(),
                "INVEROSIMIL",
                "Un dragón bloqueó la entrada de mi casa y no pude salir"
        );

        List<ar.edu.davinci.excusas.model.excusas.Excusa> excusas = excusaService.obtenerTodasLasExcusas();
        int indiceUltimaExcusa = excusas.size() - 1;
        excusaService.procesarExcusa(indiceUltimaExcusa);


        assertTrue(prontuarioService.contarProntuarios() >= 1);

        int eliminados = prontuarioService.limpiarProntuarios();
        assertTrue(eliminados >= 1);
        assertEquals(0, prontuarioService.contarProntuarios());
    }

    @Test
    @Transactional
    public void testValidacionesBasicas() {

        assertThrows(InvalidDataException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(100);
        });

        assertThrows(InvalidDataException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(1000);
        });

        assertThrows(BusinessRuleException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(9999);
        });
    }

    @Test
    @Transactional
    public void testFlujoCompleto_CrearYVerificarProntuario() {

        try {
            prontuarioService.limpiarProntuarios();
        } catch (BusinessRuleException e) {
        }

        assertEquals(0, prontuarioService.contarProntuarios());

        String nombreUnico = "Flujo Completo " + System.currentTimeMillis();
        Empleado empleado = empleadoService.crearEmpleado(nombreUnico, "flujocompleto@test.com");

        excusaService.crearExcusa(
                empleado.getLegajo(),
                "INVEROSIMIL",
                "Viajé accidentalmente en el tiempo y llegué al día equivocado"
        );

        List<ar.edu.davinci.excusas.model.excusas.Excusa> excusas = excusaService.obtenerTodasLasExcusas();
        int indiceUltimaExcusa = excusas.size() - 1;
        excusaService.procesarExcusa(indiceUltimaExcusa);

        assertEquals(1, prontuarioService.contarProntuarios());

        List<Prontuario> prontuarios = prontuarioService.obtenerTodosLosProntuarios();
        assertEquals(1, prontuarios.size());

        Prontuario prontuario = prontuarios.get(0);
        assertEquals(nombreUnico, prontuario.getEmpleado().getNombre());
        assertEquals(empleado.getLegajo(), prontuario.getLegajo());
        assertTrue(prontuario.getExcusa().getDescripcion().contains("tiempo"));

        List<Prontuario> prontuariosEmpleado = prontuarioService.obtenerProntuariosPorEmpleado(empleado.getLegajo());
        assertEquals(1, prontuariosEmpleado.size());
        assertEquals(nombreUnico, prontuariosEmpleado.get(0).getEmpleado().getNombre());
    }

    @Test
    @Transactional
    public void testComportamientoConDatosDelDataInitializer() {

        List<Prontuario> prontuarios = prontuarioService.obtenerTodosLosProntuarios();
        int cantidadInicial = prontuarios.size();

        assertEquals(cantidadInicial, prontuarioService.contarProntuarios());

        for (Prontuario prontuario : prontuarios) {
            assertNotNull(prontuario.getEmpleado());
            assertNotNull(prontuario.getEmpleado().getNombre());
            assertNotNull(prontuario.getEmpleado().getEmail());
            assertTrue(prontuario.getEmpleado().getLegajo() > 1000);

            assertNotNull(prontuario.getExcusa());
            assertNotNull(prontuario.getExcusa().getDescripcion());
            assertNotNull(prontuario.getExcusa().getMotivo());

            assertEquals(prontuario.getEmpleado().getLegajo(), prontuario.getLegajo());
        }

        for (Prontuario prontuario : prontuarios) {
            List<Prontuario> prontuariosEmpleado = prontuarioService.obtenerProntuariosPorEmpleado(
                    prontuario.getEmpleado().getLegajo()
            );
            assertFalse(prontuariosEmpleado.isEmpty());
            assertTrue(prontuariosEmpleado.stream()
                    .allMatch(p -> p.getEmpleado().getLegajo() == prontuario.getEmpleado().getLegajo()));
        }
    }
}
