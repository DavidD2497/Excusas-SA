package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.entity.ExcusaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ExcusaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExcusaRepository excusaRepository;

    @Test
    public void testSaveAndFindExcusa() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Juan Test", "juan@test.com", 1001);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa = new ExcusaEntity(empleado, "Se me hizo tarde por el tráfico", "TRIVIAL");

        
        ExcusaEntity savedExcusa = excusaRepository.save(excusa);
        entityManager.flush();

        
        assertNotNull(savedExcusa.getId());
        assertEquals("Se me hizo tarde por el tráfico", savedExcusa.getDescripcion());
        assertEquals("TRIVIAL", savedExcusa.getTipoMotivo());
        assertNotNull(savedExcusa.getFechaCreacion());
        assertFalse(savedExcusa.getProcesada());
    }

    @Test
    public void testFindByEmpleadoLegajo() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Maria Test", "maria@test.com", 1002);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado, "Primera excusa", "TRIVIAL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado, "Segunda excusa", "PROBLEMA_FAMILIAR");
        
        entityManager.persistAndFlush(excusa1);
        entityManager.persistAndFlush(excusa2);

        
        List<ExcusaEntity> excusas = excusaRepository.findByEmpleadoLegajo(1002);

        
        assertEquals(2, excusas.size());
        assertTrue(excusas.stream().anyMatch(e -> e.getDescripcion().equals("Primera excusa")));
        assertTrue(excusas.stream().anyMatch(e -> e.getDescripcion().equals("Segunda excusa")));
    }

    @Test
    public void testFindByTipoMotivoContainingIgnoreCase() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Pedro Test", "pedro@test.com", 1003);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado, "Problema con la luz", "PROBLEMA_ELECTRICO");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado, "Problema familiar", "PROBLEMA_FAMILIAR");
        ExcusaEntity excusa3 = new ExcusaEntity(empleado, "Excusa trivial", "TRIVIAL");
        
        entityManager.persistAndFlush(excusa1);
        entityManager.persistAndFlush(excusa2);
        entityManager.persistAndFlush(excusa3);

        
        List<ExcusaEntity> excusasProblema = excusaRepository.findByTipoMotivoContainingIgnoreCase("PROBLEMA");

        
        assertEquals(2, excusasProblema.size());
        assertTrue(excusasProblema.stream().anyMatch(e -> e.getTipoMotivo().equals("PROBLEMA_ELECTRICO")));
        assertTrue(excusasProblema.stream().anyMatch(e -> e.getTipoMotivo().equals("PROBLEMA_FAMILIAR")));
    }

    @Test
    public void testCountByEmpleadoLegajo() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Ana Test", "ana@test.com", 1004);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado, "Primera excusa", "TRIVIAL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado, "Segunda excusa", "COMPLEJO");
        ExcusaEntity excusa3 = new ExcusaEntity(empleado, "Tercera excusa", "INVEROSIMIL");
        
        entityManager.persistAndFlush(excusa1);
        entityManager.persistAndFlush(excusa2);
        entityManager.persistAndFlush(excusa3);

        
        long count = excusaRepository.countByEmpleadoLegajo(1004);

        
        assertEquals(3, count);
    }

    @Test
    public void testFindAll() {
        
        EmpleadoEntity empleado1 = new EmpleadoEntity("Carlos Test", "carlos@test.com", 1005);
        EmpleadoEntity empleado2 = new EmpleadoEntity("Sofia Test", "sofia@test.com", 1006);
        
        empleado1 = entityManager.persistAndFlush(empleado1);
        empleado2 = entityManager.persistAndFlush(empleado2);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado1, "Excusa de Carlos", "TRIVIAL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado2, "Excusa de Sofia", "COMPLEJO");
        
        entityManager.persistAndFlush(excusa1);
        entityManager.persistAndFlush(excusa2);

        
        List<ExcusaEntity> todasLasExcusas = excusaRepository.findAll();

        
        assertEquals(2, todasLasExcusas.size());
    }

    @Test
    public void testUpdateExcusaProcesada() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Luis Test", "luis@test.com", 1007);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa = new ExcusaEntity(empleado, "Excusa para procesar", "TRIVIAL");
        ExcusaEntity savedExcusa = entityManager.persistAndFlush(excusa);

        
        savedExcusa.setProcesada(true);
        ExcusaEntity updatedExcusa = excusaRepository.save(savedExcusa);
        entityManager.flush();

        
        assertTrue(updatedExcusa.getProcesada());
    }
}
