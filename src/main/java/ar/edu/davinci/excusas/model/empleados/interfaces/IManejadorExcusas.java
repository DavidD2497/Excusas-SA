package com.excusas.model.empleados.interfaces;

import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;

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
