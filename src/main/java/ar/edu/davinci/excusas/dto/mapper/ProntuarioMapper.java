package ar.edu.davinci.excusas.dto.mapper;

import ar.edu.davinci.excusas.entity.ProntuarioEntity;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProntuarioMapper {

    @Autowired
    private ExcusaMapper excusaMapper;

    public Prontuario toModel(ProntuarioEntity entity) {
        if (entity == null) return null;
        
        Empleado empleado = new Empleado(
            entity.getEmpleado().getNombre(),
            entity.getEmpleado().getEmail(),
            entity.getEmpleado().getLegajo()
        );
        
        Excusa excusa = excusaMapper.toModel(entity.getExcusa());
        
        return new Prontuario(empleado, excusa, entity.getLegajo());
    }
}
