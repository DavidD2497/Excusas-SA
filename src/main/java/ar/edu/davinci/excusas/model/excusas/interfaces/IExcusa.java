package ar.edu.davinci.excusas.model.excusas.interfaces;

import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import ar.edu.davinci.excusas.model.excusas.motivos.MotivoExcusa;

public interface IExcusa {
    Empleado getEmpleado();
    MotivoExcusa getMotivo();
    String getDescripcion();
    boolean puedeSerManejadaPor(IManejadorExcusas encargado);
}
