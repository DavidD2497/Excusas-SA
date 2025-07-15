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
        
        Empleado empleado = empleadoService.crearEmpleado("Juan Perez", "juan.perez@test.com");

        
        assertNotNull(empleado);
        assertEquals("Juan Perez", empleado.getNombre());
        assertEquals("juan.perez@test.com", empleado.getEmail());
        assertTrue(empleado.getLegajo() > 1000);
    }

    @Test
    @Transactional
    public void testCrearEmpleado_EmailDuplicado() {
        
        empleadoService.crearEmpleado("Maria Garcia", "maria@test.com");

        
        assertThrows(DuplicateEntityException.class, () -> {
            empleadoService.crearEmpleado("Pedro Lopez", "maria@test.com");
        });
    }

    @Test
    @Transactional
    public void testCrearEmpleado_NombreDuplicado() {
        
        empleadoService.crearEmpleado("Ana Martinez", "ana@test.com");

        
        assertThrows(DuplicateEntityException.class, () -> {
            empleadoService.crearEmpleado("Ana Martinez", "ana2@test.com");
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
        
        Empleado empleadoCreado = empleadoService.crearEmpleado("Carlos Rodriguez", "carlos@test.com");

        
        Empleado empleadoEncontrado = empleadoService.obtenerEmpleadoPorLegajo(empleadoCreado.getLegajo());

        
        assertNotNull(empleadoEncontrado);
        assertEquals("Carlos Rodriguez", empleadoEncontrado.getNombre());
        assertEquals("carlos@test.com", empleadoEncontrado.getEmail());
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
        
        empleadoService.crearEmpleado("Sofia Gonzalez", "sofia@test.com");
        empleadoService.crearEmpleado("Diego Morales", "diego@test.com");

        
        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();

        
        assertEquals(2, empleados.size());
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Sofia Gonzalez")));
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Diego Morales")));
    }

    @Test
    @Transactional
    public void testEliminarEmpleado_Success() {
        
        Empleado empleado = empleadoService.crearEmpleado("Luis Fernandez", "luis@test.com");

        
        empleadoService.eliminarEmpleado(empleado.getLegajo());

        
        assertThrows(EmpleadoNotFoundException.class, () -> {
            empleadoService.obtenerEmpleadoPorLegajo(empleado.getLegajo());
        });
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_Success() {
        
        empleadoService.crearEmpleado("Roberto Silva", "roberto@test.com");
        empleadoService.crearEmpleado("Roberto Martinez", "roberto.martinez@test.com");

        
        List<Empleado> empleados = empleadoService.buscarPorNombre("Roberto");

        
        assertEquals(2, empleados.size());
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Roberto Silva")));
        assertTrue(empleados.stream().anyMatch(e -> e.getNombre().equals("Roberto Martinez")));
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_NotFound() {
        
        assertThrows(EmpleadoNotFoundException.class, () -> {
            empleadoService.buscarPorNombre("NoExiste");
        });
    }

    @Test
    @Transactional
    public void testBuscarPorNombre_NombreMuyCorto() {
        
        assertThrows(InvalidDataException.class, () -> {
            empleadoService.buscarPorNombre("A");
        });
    }
}
