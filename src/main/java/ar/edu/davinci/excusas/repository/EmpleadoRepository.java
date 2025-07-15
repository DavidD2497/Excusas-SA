package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {
    
    Optional<EmpleadoEntity> findByLegajo(Integer legajo);
    
    Optional<EmpleadoEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT e FROM EmpleadoEntity e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<EmpleadoEntity> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
