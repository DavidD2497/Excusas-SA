package com.excusas.model.empleados.encargados.modos.interfaces;

import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.Excusa;

public interface IModoManejo {
    void manejar(IManejadorExcusas encargado, Excusa excusa);
}
