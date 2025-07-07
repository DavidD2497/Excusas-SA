package com.excusas.model.excusas.motivos;

import com.excusas.model.empleados.interfaces.IManejadorExcusas;

public class MotivoInverosimil extends MotivoExcusa {

    @Override
    public boolean esAceptablePor(IManejadorExcusas encargado) {
        return encargado.puedeManejarInverosimil();
    }
}
