package com.excusas.model.excusas.motivos;

import com.excusas.model.empleados.encargados.SupervisorArea;
import com.excusas.model.excusas.Excusa;

public class MotivoProblemaElectrico extends MotivoModerado {

    @Override
    public void procesarConSupervisor(SupervisorArea supervisor, Excusa excusa) {
        supervisor.procesarProblemaElectrico(excusa);
    }
}
