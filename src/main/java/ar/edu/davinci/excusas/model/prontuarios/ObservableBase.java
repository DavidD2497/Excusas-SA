package com.excusas.model.prontuarios;

import com.excusas.model.prontuarios.interfaces.IObservable;
import com.excusas.model.prontuarios.interfaces.IObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class ObservableBase implements IObservable {

    private final List<IObserver> observadores;

    protected ObservableBase() {
        this.observadores = new ArrayList<>();
    }

    @Override
    public final void agregarObservador(IObserver observador) {
        this.observadores.add(observador);
    }

    @Override
    public final void eliminarObservador(IObserver observador) {
        this.observadores.remove(observador);
    }

    @Override
    public final void notificarObservadores(Prontuario prontuario) {
        List<IObserver> observadoresCopia = new ArrayList<>(this.observadores);
        for (IObserver observador : observadoresCopia) {
            observador.actualizar(prontuario);
        }
    }
}
