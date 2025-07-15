package ar.edu.davinci.excusas.model.excusas.motivos;

import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import ar.edu.davinci.excusas.model.empleados.encargados.SupervisorArea;
import ar.edu.davinci.excusas.model.excusas.Excusa;

public abstract class MotivoExcusa {

    public abstract boolean esAceptablePor(IManejadorExcusas encargado);

    public void procesarConSupervisor(SupervisorArea supervisor, Excusa excusa) {
        supervisor.procesarMotivoModeradoGenerico(excusa);
    }
}
