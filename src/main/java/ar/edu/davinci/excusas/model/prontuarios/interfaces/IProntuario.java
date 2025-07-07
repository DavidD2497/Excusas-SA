package com.excusas.model.prontuarios.interfaces;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.excusas.Excusa;

public interface IProntuario {
    Empleado getEmpleado();
    Excusa getExcusa();
    int getLegajo();
}
