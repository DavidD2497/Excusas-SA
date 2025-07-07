package ar.edu.davinci.excusas.model.prontuarios.interfaces;

import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.excusas.Excusa;

public interface IProntuario {
    Empleado getEmpleado();
    Excusa getExcusa();
    int getLegajo();
}
