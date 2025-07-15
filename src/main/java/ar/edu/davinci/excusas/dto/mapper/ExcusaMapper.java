package ar.edu.davinci.excusas.dto.mapper;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.entity.ExcusaEntity;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.*;
import org.springframework.stereotype.Component;

@Component
public class ExcusaMapper {

    public ExcusaEntity toEntity(Empleado empleado, String tipoMotivo, String descripcion, EmpleadoEntity empleadoEntity) {
        return new ExcusaEntity(empleadoEntity, descripcion, tipoMotivo);
    }

    public Excusa toModel(ExcusaEntity entity) {
        if (entity == null) return null;
        
        Empleado empleado = new Empleado(
            entity.getEmpleado().getNombre(),
            entity.getEmpleado().getEmail(),
            entity.getEmpleado().getLegajo()
        );
        
        return new Excusa(empleado, crearMotivo(entity.getTipoMotivo()), entity.getDescripcion());
    }

    private MotivoExcusa crearMotivo(String tipoMotivo) {
        String tipo = tipoMotivo.toUpperCase();
        return switch (tipo) {
            case "TRIVIAL" -> new MotivoTrivial();
            case "PROBLEMA_ELECTRICO" -> new MotivoProblemaElectrico();
            case "PROBLEMA_FAMILIAR" -> new MotivoProblemaFamiliar();
            case "COMPLEJO" -> new MotivoComplejo();
            case "INVEROSIMIL" -> new MotivoInverosimil();
            default -> new MotivoTrivial();
        };
    }
}
