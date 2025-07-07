package com.excusas.model.empleados;

import com.excusas.model.empleados.interfaces.IEncargado;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;
import com.excusas.model.empleados.encargados.modos.ModoNormal;
import com.excusas.model.excusas.Excusa;

public abstract class Encargado extends Empleado implements IEncargado {

    private IManejadorExcusas siguiente;
    private IModoManejo modo;

    public Encargado(String nombre, String email, int legajo) {
        super(nombre, email, legajo);
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
    public final void ejecutarProcesamiento(Excusa excusa) {
        if (excusa.puedeSerManejadaPor(this)) {
            this.procesarExcusa(excusa);
        } else {
            this.getSiguiente().manejarExcusa(excusa);
        }
    }

    @Override
    public abstract void procesarExcusa(Excusa excusa);

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
        return this.getEmail();
    }
}
