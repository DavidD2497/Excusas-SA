package com.excusas.model.empleados.encargados.modos;

import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;

public class ModoNormal implements IModoManejo {

    @Override
    public void manejar(IManejadorExcusas encargado, Excusa excusa) {
        encargado.ejecutarProcesamiento(excusa);
    }
}
