package com.excusas.model.empleados.interfaces;

import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoExcusa;

public interface IEmpleado {
    String getNombre();
    String getEmail();
    int getLegajo();
    Excusa crearExcusa(MotivoExcusa motivo, String descripcion);
}
