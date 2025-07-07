package com.excusas.model.empleados.encargados;

import com.excusas.model.email.EmailSenderConcreto;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;
import com.excusas.model.empleados.encargados.modos.ModoNormal;
import com.excusas.model.excusas.Excusa;

public class EncargadoPorDefecto implements IManejadorExcusas {

    private IManejadorExcusas siguiente;
    private IModoManejo modo;

    public EncargadoPorDefecto() {
        this.modo = new ModoNormal();
    }

    @Override
    public void setSiguiente(IManejadorExcusas siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public IManejadorExcusas getSiguiente() {
        return this.siguiente;
    }

    @Override
    public void setModo(IModoManejo modo) {
        this.modo = modo;
    }

    @Override
    public IModoManejo getModo() {
        return this.modo;
    }

    @Override
    public void manejarExcusa(Excusa excusa) {
        this.modo.manejar(this, excusa);
    }

    @Override
    public void ejecutarProcesamiento(Excusa excusa) {
        this.procesarExcusa(excusa);
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        System.out.println("Excusa rechazada: necesitamos pruebas contundentes");

        new EmailSenderConcreto().enviarEmail(
                excusa.getEmailEmpleado(),
                "sistema@excusas.com",
                "Excusa rechazada",
                "Excusa rechazada: necesitamos pruebas contundentes"
        );
    }

    @Override
    public boolean puedeManejarTrivial() {
        return false;
    }

    @Override
    public boolean puedeManejarModerado() {
        return false;
    }

    @Override
    public boolean puedeManejarComplejo() {
        return false;
    }

    @Override
    public boolean puedeManejarInverosimil() {
        return false;
    }

    @Override
    public String getEmailOrigen() {
        return "sistema@excusas.com";
    }
}
