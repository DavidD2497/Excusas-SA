package ar.edu.davinci.excusas.model.prontuarios.interfaces;

import ar.edu.davinci.excusas.model.prontuarios.Prontuario;

public interface IObservable {
    void agregarObservador(IObserver observador);
    void eliminarObservador(IObserver observador);
    void notificarObservadores(Prontuario prontuario);
}