package ar.edu.davinci.excusas.model.empleados.encargados.modos.interfaces;

import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import ar.edu.davinci.excusas.model.excusas.Excusa;

public interface IModoManejo {
    void manejar(IManejadorExcusas encargado, Excusa excusa);
}
