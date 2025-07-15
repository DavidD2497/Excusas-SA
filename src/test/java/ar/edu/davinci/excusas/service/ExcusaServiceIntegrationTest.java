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
        
        Empleado empleado = empleadoService.crearEmpleado("Juan Test", "juan.test@excusas.com");

        
        Excusa excusa = excusaService.crearExcusa(
                empleado.getLegajo(),
                "TRIVIAL",
                "Se me hizo tarde por el tráfico en la autopista"
        );

        
        assertNotNull(excusa);
        assertEquals("Juan Test", excusa.getNombreEmpleado());
        assertEquals("Se me hizo tarde por el tráfico en la autopista", excusa.getDescripcion());
        assertEquals("MotivoTrivial", excusa.getMotivo().getClass().getSimpleName());
    }

    @Test
    @Transactional
    public void testCrearExcusa_LimiteExcusasExcedido() {
        
        Empleado empleado = empleadoService.crearEmpleado("Maria Limite", "maria.limite@test.com");

        
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
        
        Empleado empleado1 = empleadoService.crearEmpleado("Pedro Test", "pedro.test@test.com");
        Empleado empleado2 = empleadoService.crearEmpleado("Ana Test", "ana.test@test.com");

        excusaService.crearExcusa(empleado1.getLegajo(), "TRIVIAL", "Excusa de Pedro con descripción larga");
        excusaService.crearExcusa(empleado2.getLegajo(), "COMPLEJO", "Excusa de Ana con descripción compleja y larga");

        
        List<Excusa> excusas = excusaService.obtenerTodasLasExcusas();

        
        assertEquals(2, excusas.size());
        assertTrue(excusas.stream().anyMatch(e -> e.getNombreEmpleado().equals("Pedro Test")));
        assertTrue(excusas.stream().anyMatch(e -> e.getNombreEmpleado().equals("Ana Test")));
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorEmpleado_Success() {
        
        Empleado empleado = empleadoService.crearEmpleado("Carlos Test", "carlos.test@test.com");
        
        excusaService.crearExcusa(empleado.getLegajo(), "PROBLEMA_FAMILIAR", "Primera excusa familiar con descripción larga");
        excusaService.crearExcusa(empleado.getLegajo(), "PROBLEMA_ELECTRICO", "Segunda excusa eléctrica con descripción larga");

        
        List<Excusa> excusas = excusaService.obtenerExcusasPorEmpleado(empleado.getLegajo());

        
        assertEquals(2, excusas.size());
        assertTrue(excusas.stream().allMatch(e -> e.getNombreEmpleado().equals("Carlos Test")));
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorEmpleado_NotFound() {
        
        Empleado empleado = empleadoService.crearEmpleado("Sofia Test", "sofia.test@test.com");

        
        assertThrows(ExcusaNotFoundException.class, () -> {
            excusaService.obtenerExcusasPorEmpleado(empleado.getLegajo());
        });
    }

    @Test
    @Transactional
    public void testObtenerExcusasPorTipoMotivo_Success() {
        
        Empleado empleado1 = empleadoService.crearEmpleado("Luis Test", "luis.test@test.com");
        Empleado empleado2 = empleadoService.crearEmpleado("Diego Test", "diego.test@test.com");

        excusaService.crearExcusa(empleado1.getLegajo(), "PROBLEMA_FAMILIAR", "Problema familiar de Luis con descripción larga");
        excusaService.crearExcusa(empleado2.getLegajo(), "PROBLEMA_FAMILIAR", "Problema familiar de Diego con descripción larga");
        excusaService.crearExcusa(empleado1.getLegajo(), "TRIVIAL", "Excusa trivial de Luis con descripción larga");

        
        List<Excusa> excusasFamiliares = excusaService.obtenerExcusasPorTipoMotivo("PROBLEMA_FAMILIAR");

        
        assertEquals(2, excusasFamiliares.size());
        assertTrue(excusasFamiliares.stream().allMatch(e -> 
            e.getMotivo().getClass().getSimpleName().equals("MotivoProblemaFamiliar")));
    }

    @Test
    @Transactional
    public void testCrearExcusa_TipoMotivoInvalido() {
        
        Empleado empleado = empleadoService.crearEmpleado("Roberto Test", "roberto.test@test.com");

        
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
        
        Empleado empleado = empleadoService.crearEmpleado("Valeria Test", "valeria.test@test.com");

        
        assertThrows(InvalidDataException.class, () -> {
            excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", "Corta");
        });
    }

    @Test
    @Transactional
    public void testProcesarExcusa_Success() {
        
        Empleado empleado = empleadoService.crearEmpleado("Procesamiento Test", "procesamiento.test@test.com");
        excusaService.crearExcusa(empleado.getLegajo(), "TRIVIAL", "Excusa para procesar con descripción larga");

        
        assertDoesNotThrow(() -> {
            excusaService.procesarExcusa(0);
        });
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
        
        assertThrows(ExcusaNotFoundException.class, () -> {
            excusaService.procesarExcusa(999);
        });
    }
}
