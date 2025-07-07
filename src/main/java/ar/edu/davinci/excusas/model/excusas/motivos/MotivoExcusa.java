package com.excusas.model.excusas.motivos;

import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.empleados.encargados.SupervisorArea;
import com.excusas.model.excusas.Excusa;

public abstract class MotivoExcusa {

    public abstract boolean esAceptablePor(IManejadorExcusas encargado);

    public void procesarConSupervisor(SupervisorArea supervisor, Excusa excusa) {
        supervisor.procesarMotivoModeradoGenerico(excusa);
    }
}

