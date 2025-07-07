package com.excusas.model.excusas.motivos;

import com.excusas.model.empleados.interfaces.IManejadorExcusas;

public class MotivoComplejo extends MotivoExcusa {

    @Override
    public boolean esAceptablePor(IManejadorExcusas encargado) {
        return encargado.puedeManejarComplejo();
    }
}
