package com.excusas.model.prontuarios.interfaces;

import com.excusas.model.prontuarios.Prontuario;

public interface IObservable {
    void agregarObservador(IObserver observador);
    void eliminarObservador(IObserver observador);
    void notificarObservadores(Prontuario prontuario);
}