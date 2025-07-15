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
    public void testObtenerTodosLosProntuarios_Vacio() {
        
        List<Prontuario> prontuarios = prontuarioService.obtenerTodosLosProntuarios();

        
        assertTrue(prontuarios.isEmpty());
    }

    @Test
    @Transactional
    public void testContarProntuarios_Cero() {
        
        int count = prontuarioService.contarProntuarios();

        
        assertEquals(0, count);
    }

    @Test
    @Transactional
    public void testLimpiarProntuarios_SinProntuarios() {
        
        assertThrows(BusinessRuleException.class, () -> {
            prontuarioService.limpiarProntuarios();
        });
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
        
        Empleado empleado = empleadoService.crearEmpleado("Test Empleado", "test@test.com");

        
        assertThrows(BusinessRuleException.class, () -> {
            prontuarioService.obtenerProntuariosPorEmpleado(empleado.getLegajo());
        });
    }

    
    
    
}
