package ar.edu.davinci.excusas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "excusas")
public class ExcusaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipoMotivo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean procesada = false;

    public ExcusaEntity() {}

    public ExcusaEntity(EmpleadoEntity empleado, String descripcion, String tipoMotivo) {
        this.empleado = empleado;
        this.descripcion = descripcion;
        this.tipoMotivo = tipoMotivo;
        this.fechaCreacion = LocalDateTime.now();
        this.procesada = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmpleadoEntity getEmpleado() { return empleado; }
    public void setEmpleado(EmpleadoEntity empleado) { this.empleado = empleado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoMotivo() { return tipoMotivo; }
    public void setTipoMotivo(String tipoMotivo) { this.tipoMotivo = tipoMotivo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Boolean getProcesada() { return procesada; }
    public void setProcesada(Boolean procesada) { this.procesada = procesada; }
}
