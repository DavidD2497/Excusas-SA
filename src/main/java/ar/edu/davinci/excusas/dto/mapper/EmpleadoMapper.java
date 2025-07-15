package ar.edu.davinci.excusas.dto.mapper;

import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    public EmpleadoEntity toEntity(Empleado empleado) {
        if (empleado == null) return null;
        return new EmpleadoEntity(empleado.getNombre(), empleado.getEmail(), empleado.getLegajo());
    }

    public Empleado toModel(EmpleadoEntity entity) {
        if (entity == null) return null;
        return new Empleado(entity.getNombre(), entity.getEmail(), entity.getLegajo());
    }

    public EmpleadoEntity toEntity(String nombre, String email, Integer legajo) {
        return new EmpleadoEntity(nombre, email, legajo);
    }
}
