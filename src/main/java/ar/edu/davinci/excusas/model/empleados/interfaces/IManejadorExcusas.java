package ar.edu.davinci.excusas.model.empleados.interfaces;

import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;

public interface IManejadorExcusas {
    void setSiguiente(IManejadorExcusas siguiente);
    IManejadorExcusas getSiguiente();
    void setModo(IModoManejo modo);
    IModoManejo getModo();
    void manejarExcusa(Excusa excusa);
    void procesarExcusa(Excusa excusa);
    void ejecutarProcesamiento(Excusa excusa);
    boolean puedeManejarTrivial();
    boolean puedeManejarModerado();
    boolean puedeManejarComplejo();
    boolean puedeManejarInverosimil();
    String getEmailOrigen();
}
