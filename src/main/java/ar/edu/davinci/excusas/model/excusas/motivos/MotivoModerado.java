package ar.edu.davinci.excusas.model.excusas.motivos;

import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;

public abstract class MotivoModerado extends MotivoExcusa {

    @Override
    public final boolean esAceptablePor(IManejadorExcusas encargado) {
        return encargado.puedeManejarModerado();
    }
}


