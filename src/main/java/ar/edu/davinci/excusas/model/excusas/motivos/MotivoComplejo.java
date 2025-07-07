package ar.edu.davinci.excusas.model.excusas.motivos;

import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;

public class MotivoComplejo extends MotivoExcusa {

    @Override
    public boolean esAceptablePor(IManejadorExcusas encargado) {
        return encargado.puedeManejarComplejo();
    }
}
