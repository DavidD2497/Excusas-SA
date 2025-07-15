package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.DuplicateEntityException;
import ar.edu.davinci.excusas.exception.EmpleadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmpleadoServiceIntegrationTest {

    @Autowired
    private EmpleadoService empleadoService;

    @Test
    @Transactional
    public void testCrearEmpleado_Success() {

        Empleado empleado = empleadoService.crearEmpleado("Juan Perez Test", "juan.perez.test@test.com");


        assertNotNull(empleado);
        assertEquals("Juan Perez Test", empleado.getNombre());
        assertEquals("juan.perez.test@test.com", empleado.getEmail());
        assertTrue(empleado.getLegajo() > 1000);
    }

    @Test
    @Transactional
    public void testCrearEmpleado_EmailDuplicado() {

        empleadoService.crearEmpleado("Maria Garcia Test", "maria.test@test.com");


        assertThrows(DuplicateEntityException.class, () -> {
            empleadoService.crearEmpleado("Pedro Lopez Test", "maria.test@test.com");
        });
    }

    @Test
    @Transactional
    public void testCrearEmpleado_NombreDuplicado() {

        String nombreUnico = "Ana Martinez Test " + System.currentTimeMillis();
        empleadoService.crearEmpleado(nombreUnico, "ana.test@test.com");


        assertThrows(DuplicateEntityException.class, () -> {
            empleadoService.crearEmpleado(nombreUnico, "ana2.test@test.com");
        });
    }

    @Test
    @Transactional
    public void testCrearEmpleado_DatosInvalidos() {

        assertThrows(InvalidDataException.class, () -> {
            empleadoService.crearEmpleado("", "email-invalido");
        });
    }

    @Test
    @Transactional
    public void testObtenerEmpleadoPorLegajo_Success() {

        Empleado empleadoCreado = empleadoService.crearEmpleado("Carlos Rodriguez Test", "carlos.test@test.com");


        Empleado empleadoEncontrado = empleadoService.obtenerEmpleadoPorLegajo(empleadoCreado.getLegajo());


        assertNotNull(empleadoEncontrado);
        assertEquals("Carlos Rodriguez Test", empleadoEncontrado.getNombre());
        assertEquals("carlos.test@test.com", empleadoEncontrado.getEmail());
        assertEquals(empleadoCreado.getLegajo(), empleadoEncontrado.getLegajo());
    }

    @Test
    @Transactional
    public void testObtenerEmpleadoPorLegajo_NotFound() {

        assertThrows(EmpleadoNotFoundException.class, () -> {
            empleadoService.obtenerEmpleadoPorLegajo(9999);
        });
    }

    @Test
    @Transactional
    public void testObtenerTodosLosEmpleados() {

        int empleadosIniciales = empleadoService.obtenerTodosLosEmpleados().size();

        empleadoService.crearEmpleado("Sofia Test Unique", "sofia.test@test.com");
        empleadoService.crearEmpleado("Diego Test Unique", "diego.test@test.com");


        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();


        assertEquals(empleadosIniciales + 2, empleados.size());
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Sofia Test Unique")));
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Diego Test Unique")));
    }

    @Test
    @Transactional
    public void testEliminarEmpleado_Success() {

        Empleado empleado = empleadoService.crearEmpleado("Luis Fernandez Test", "luis.test@test.com");


        empleadoService.eliminarEmpleado(empleado.getLegajo());


        assertThrows(EmpleadoNotFoundException.class, () -> {
            empleadoService.obtenerEmpleadoPorLegajo(empleado.getLegajo());
        });
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_Success() {

        String nombreBase = "Roberto Test " + System.currentTimeMillis();
        empleadoService.crearEmpleado(nombreBase + " Silva", "roberto.silva@test.com");
        empleadoService.crearEmpleado(nombreBase + " Martinez", "roberto.martinez@test.com");


        List<Empleado> empleados = empleadoService.buscarPorNombre(nombreBase);


        assertEquals(2, empleados.size());
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().contains("Silva")));
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().contains("Martinez")));
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_NotFound() {

        String nombreInexistente = "NoExiste" + System.currentTimeMillis();

        assertThrows(EmpleadoNotFoundException.class, () -> {
            empleadoService.buscarPorNombre(nombreInexistente);
        });
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_NombreMuyCorto() {

        assertThrows(InvalidDataException.class, () -> {
            empleadoService.buscarPorNombre("A");
        });
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_ConDatosExistentes() {

        List<Empleado> empleadosJuan = empleadoService.buscarPorNombre("Juan");

        assertFalse(empleadosJuan.isEmpty());
        assertTrue(empleadosJuan.stream().anyMatch(e -> e.getNombre().contains("Juan")));
    }

    @Test
    @Transactional
    public void testObtenerEmpleadoPorLegajo_DatosExistentes() {

        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();
        assertFalse(empleados.isEmpty(), "Debería haber empleados del DataInitializer");

        Empleado primerEmpleado = empleados.get(0);
        Empleado empleadoEncontrado = empleadoService.obtenerEmpleadoPorLegajo(primerEmpleado.getLegajo());

        assertNotNull(empleadoEncontrado);
        assertEquals(primerEmpleado.getLegajo(), empleadoEncontrado.getLegajo());
        assertEquals(primerEmpleado.getNombre(), empleadoEncontrado.getNombre());
        assertEquals(primerEmpleado.getEmail(), empleadoEncontrado.getEmail());
    }
}
