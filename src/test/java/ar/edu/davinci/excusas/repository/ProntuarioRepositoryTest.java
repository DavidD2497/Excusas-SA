package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.entity.ExcusaEntity;
import ar.edu.davinci.excusas.entity.ProntuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProntuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Test
    public void testSaveAndFindProntuario() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Juan Prontuario", "juan.prontuario@test.com", 1001);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa = new ExcusaEntity(empleado, "Excusa inverosímil", "INVEROSIMIL");
        excusa = entityManager.persistAndFlush(excusa);

        ProntuarioEntity prontuario = new ProntuarioEntity(empleado, excusa, 1001);

        
        ProntuarioEntity savedProntuario = prontuarioRepository.save(prontuario);
        entityManager.flush();

        
        assertNotNull(savedProntuario.getId());
        assertEquals(1001, savedProntuario.getLegajo());
        assertNotNull(savedProntuario.getFechaCreacion());
        assertEquals("Juan Prontuario", savedProntuario.getEmpleado().getNombre());
        assertEquals("Excusa inverosímil", savedProntuario.getExcusa().getDescripcion());
    }

    @Test
    public void testFindByLegajo() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Maria Prontuario", "maria.prontuario@test.com", 1002);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado, "Primera excusa inverosímil", "INVEROSIMIL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado, "Segunda excusa inverosímil", "INVEROSIMIL");
        
        excusa1 = entityManager.persistAndFlush(excusa1);
        excusa2 = entityManager.persistAndFlush(excusa2);

        ProntuarioEntity prontuario1 = new ProntuarioEntity(empleado, excusa1, 1002);
        ProntuarioEntity prontuario2 = new ProntuarioEntity(empleado, excusa2, 1002);
        
        entityManager.persistAndFlush(prontuario1);
        entityManager.persistAndFlush(prontuario2);

        
        List<ProntuarioEntity> prontuarios = prontuarioRepository.findByLegajo(1002);

        
        assertEquals(2, prontuarios.size());
        assertTrue(prontuarios.stream().allMatch(p -> p.getLegajo().equals(1002)));
        assertTrue(prontuarios.stream().anyMatch(p -> p.getExcusa().getDescripcion().equals("Primera excusa inverosímil")));
        assertTrue(prontuarios.stream().anyMatch(p -> p.getExcusa().getDescripcion().equals("Segunda excusa inverosímil")));
    }

    @Test
    public void testFindAll() {
        
        EmpleadoEntity empleado1 = new EmpleadoEntity("Pedro Prontuario", "pedro.prontuario@test.com", 1003);
        EmpleadoEntity empleado2 = new EmpleadoEntity("Ana Prontuario", "ana.prontuario@test.com", 1004);
        
        empleado1 = entityManager.persistAndFlush(empleado1);
        empleado2 = entityManager.persistAndFlush(empleado2);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado1, "Excusa de Pedro", "INVEROSIMIL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado2, "Excusa de Ana", "INVEROSIMIL");
        
        excusa1 = entityManager.persistAndFlush(excusa1);
        excusa2 = entityManager.persistAndFlush(excusa2);

        ProntuarioEntity prontuario1 = new ProntuarioEntity(empleado1, excusa1, 1003);
        ProntuarioEntity prontuario2 = new ProntuarioEntity(empleado2, excusa2, 1004);
        
        entityManager.persistAndFlush(prontuario1);
        entityManager.persistAndFlush(prontuario2);

        
        List<ProntuarioEntity> todosProntuarios = prontuarioRepository.findAll();

        
        assertEquals(2, todosProntuarios.size());
    }

    @Test
    public void testCount() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Carlos Count", "carlos.count@test.com", 1005);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa1 = new ExcusaEntity(empleado, "Primera excusa", "INVEROSIMIL");
        ExcusaEntity excusa2 = new ExcusaEntity(empleado, "Segunda excusa", "INVEROSIMIL");
        ExcusaEntity excusa3 = new ExcusaEntity(empleado, "Tercera excusa", "INVEROSIMIL");
        
        excusa1 = entityManager.persistAndFlush(excusa1);
        excusa2 = entityManager.persistAndFlush(excusa2);
        excusa3 = entityManager.persistAndFlush(excusa3);

        ProntuarioEntity prontuario1 = new ProntuarioEntity(empleado, excusa1, 1005);
        ProntuarioEntity prontuario2 = new ProntuarioEntity(empleado, excusa2, 1005);
        ProntuarioEntity prontuario3 = new ProntuarioEntity(empleado, excusa3, 1005);
        
        entityManager.persistAndFlush(prontuario1);
        entityManager.persistAndFlush(prontuario2);
        entityManager.persistAndFlush(prontuario3);

        
        long count = prontuarioRepository.count();

        
        assertEquals(3, count);
    }

    @Test
    public void testDeleteAll() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Sofia Delete", "sofia.delete@test.com", 1006);
        empleado = entityManager.persistAndFlush(empleado);

        ExcusaEntity excusa = new ExcusaEntity(empleado, "Excusa para eliminar", "INVEROSIMIL");
        excusa = entityManager.persistAndFlush(excusa);

        ProntuarioEntity prontuario = new ProntuarioEntity(empleado, excusa, 1006);
        entityManager.persistAndFlush(prontuario);

        
        prontuarioRepository.deleteAll();
        entityManager.flush();

        
        assertEquals(0, prontuarioRepository.count());
    }
}
