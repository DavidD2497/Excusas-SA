package ar.edu.davinci.excusas.model.empleados.encargados.modos;

import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;

public class ModoVago implements IModoManejo {

    @Override
    public void manejar(IManejadorExcusas encargado, Excusa excusa) {
        encargado.getSiguiente().manejarExcusa(excusa);
    }
}
