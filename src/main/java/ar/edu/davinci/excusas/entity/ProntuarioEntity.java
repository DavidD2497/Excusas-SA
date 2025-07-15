package ar.edu.davinci.excusas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prontuarios")
public class ProntuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excusa_id", nullable = false)
    private ExcusaEntity excusa;

    @Column(nullable = false)
    private Integer legajo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public ProntuarioEntity() {}

    public ProntuarioEntity(EmpleadoEntity empleado, ExcusaEntity excusa, Integer legajo) {
        this.empleado = empleado;
        this.excusa = excusa;
        this.legajo = legajo;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmpleadoEntity getEmpleado() { return empleado; }
    public void setEmpleado(EmpleadoEntity empleado) { this.empleado = empleado; }

    public ExcusaEntity getExcusa() { return excusa; }
    public void setExcusa(ExcusaEntity excusa) { this.excusa = excusa; }

    public Integer getLegajo() { return legajo; }
    public void setLegajo(Integer legajo) { this.legajo = legajo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
