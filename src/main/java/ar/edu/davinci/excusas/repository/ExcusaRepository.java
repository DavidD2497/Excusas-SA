package ar.edu.davinci.excusas.repository;

import ar.edu.davinci.excusas.entity.ExcusaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ExcusaRepository extends JpaRepository<ExcusaEntity, Long> {
    
    List<ExcusaEntity> findByEmpleadoLegajo(Integer legajo);
    
    @Query("SELECT e FROM ExcusaEntity e WHERE UPPER(e.tipoMotivo) LIKE UPPER(CONCAT('%', :tipoMotivo, '%'))")
    List<ExcusaEntity> findByTipoMotivoContainingIgnoreCase(@Param("tipoMotivo") String tipoMotivo);
    
    long countByEmpleadoLegajo(Integer legajo);

    List<ExcusaEntity> findByProcesada(Boolean procesada);

    List<ExcusaEntity> findByEmpleadoLegajoAndFechaCreacionBetween(Integer legajo, LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    List<ExcusaEntity> findByEmpleadoLegajoAndFechaCreacionGreaterThanEqual(Integer legajo, LocalDateTime fechaDesde);

    List<ExcusaEntity> findByEmpleadoLegajoAndFechaCreacionLessThanEqual(Integer legajo, LocalDateTime fechaHasta);

    List<ExcusaEntity> findByFechaCreacionLessThanEqual(LocalDateTime fechaLimite);
}
