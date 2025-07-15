package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.ExcusaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcusaRepository extends JpaRepository<ExcusaEntity, Long> {
    
    List<ExcusaEntity> findByEmpleadoLegajo(Integer legajo);
    
    @Query("SELECT e FROM ExcusaEntity e WHERE UPPER(e.tipoMotivo) LIKE UPPER(CONCAT('%', :tipoMotivo, '%'))")
    List<ExcusaEntity> findByTipoMotivoContainingIgnoreCase(@Param("tipoMotivo") String tipoMotivo);
    
    long countByEmpleadoLegajo(Integer legajo);
}
