package ar.edu.davinci.excusas.model.excusas.motivos;

import ar.edu.davinci.excusas.model.empleados.encargados.SupervisorArea;
import ar.edu.davinci.excusas.model.excusas.Excusa;

public class MotivoProblemaFamiliar extends MotivoModerado {

    @Override
    public void procesarConSupervisor(SupervisorArea supervisor, Excusa excusa) {
        supervisor.procesarProblemaFamiliar(excusa);
    }
}
