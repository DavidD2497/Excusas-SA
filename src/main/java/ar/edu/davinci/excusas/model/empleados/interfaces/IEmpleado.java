package ar.edu.davinci.excusas.model.empleados.interfaces;

import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.MotivoExcusa;

public interface IEmpleado {
    String getNombre();
    String getEmail();
    int getLegajo();
    Excusa crearExcusa(MotivoExcusa motivo, String descripcion);
}
