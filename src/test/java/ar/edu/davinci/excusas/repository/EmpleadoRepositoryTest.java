package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmpleadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Test
    public void testSaveAndFindEmpleado() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Juan Perez", "juan@test.com", 1001);

        
        EmpleadoEntity savedEmpleado = empleadoRepository.save(empleado);
        entityManager.flush();

        
        assertNotNull(savedEmpleado.getId());
        assertEquals("Juan Perez", savedEmpleado.getNombre());
        assertEquals("juan@test.com", savedEmpleado.getEmail());
        assertEquals(1001, savedEmpleado.getLegajo());
    }

    @Test
    public void testFindByLegajo() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Maria Garcia", "maria@test.com", 1002);
        entityManager.persistAndFlush(empleado);

        
        Optional<EmpleadoEntity> found = empleadoRepository.findByLegajo(1002);

        
        assertTrue(found.isPresent());
        assertEquals("Maria Garcia", found.get().getNombre());
        assertEquals("maria@test.com", found.get().getEmail());
    }

    @Test
    public void testFindByLegajo_NotFound() {
        
        Optional<EmpleadoEntity> found = empleadoRepository.findByLegajo(9999);

        
        assertFalse(found.isPresent());
    }

    @Test
    public void testExistsByEmail() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Pedro Lopez", "pedro@test.com", 1003);
        entityManager.persistAndFlush(empleado);

        
        assertTrue(empleadoRepository.existsByEmail("pedro@test.com"));
        assertFalse(empleadoRepository.existsByEmail("noexiste@test.com"));
    }

    @Test
    public void testExistsByNombre() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Ana Martinez", "ana@test.com", 1004);
        entityManager.persistAndFlush(empleado);

        
        assertTrue(empleadoRepository.existsByNombre("Ana Martinez"));
        assertFalse(empleadoRepository.existsByNombre("No Existe"));
    }

    @Test
    public void testFindByNombreContainingIgnoreCase_CaseInsensitive() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("CARLOS UPPERCASE", "carlos.upper@test.com", 3001);
        entityManager.persistAndFlush(empleado);

        
        List<EmpleadoEntity> foundLower = empleadoRepository.findByNombreContainingIgnoreCase("carlos");
        List<EmpleadoEntity> foundUpper = empleadoRepository.findByNombreContainingIgnoreCase("CARLOS");
        List<EmpleadoEntity> foundMixed = empleadoRepository.findByNombreContainingIgnoreCase("Carlos");

        
        assertEquals(1, foundLower.size());
        assertEquals(1, foundUpper.size());
        assertEquals(1, foundMixed.size());
        assertEquals("CARLOS UPPERCASE", foundLower.get(0).getNombre());
    }

    @Test
    public void testFindAll() {
        
        EmpleadoEntity empleado1 = new EmpleadoEntity("Sofia Gonzalez", "sofia@test.com", 1008);
        EmpleadoEntity empleado2 = new EmpleadoEntity("Diego Morales", "diego@test.com", 1009);
        
        entityManager.persistAndFlush(empleado1);
        entityManager.persistAndFlush(empleado2);

        
        List<EmpleadoEntity> all = empleadoRepository.findAll();

        
        assertEquals(2, all.size());
    }

    @Test
    public void testDeleteEmpleado() {
        
        EmpleadoEntity empleado = new EmpleadoEntity("Roberto Silva", "roberto@test.com", 1010);
        EmpleadoEntity saved = entityManager.persistAndFlush(empleado);

        
        empleadoRepository.delete(saved);
        entityManager.flush();

        
        Optional<EmpleadoEntity> found = empleadoRepository.findByLegajo(1010);
        assertFalse(found.isPresent());
    }
}
