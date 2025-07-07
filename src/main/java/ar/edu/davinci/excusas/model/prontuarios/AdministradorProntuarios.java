package com.excusas.model.prontuarios;

import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.interfaces.IEncargado;
import java.util.ArrayList;
import java.util.List;

public class AdministradorProntuarios extends ObservableBase {

    private static final AdministradorProntuarios INSTANCIA = new AdministradorProntuarios();
    private final List<Prontuario> prontuarios;

    private AdministradorProntuarios() {
        super();
        this.prontuarios = new ArrayList<>();
    }

    public static AdministradorProntuarios getInstance() {
        return INSTANCIA;
    }

    public void notificarExcusaProcesada(Excusa excusa, IEncargado encargadoProcesador) {
        if (this.debeCrearProntuario(excusa, encargadoProcesador)) {
            Prontuario prontuario = this.crearProntuario(excusa);
            this.agregarProntuario(prontuario);
        }
    }

    private boolean debeCrearProntuario(Excusa excusa, IEncargado encargadoProcesador) {
        return encargadoProcesador.puedeManejarInverosimil() &&
                excusa.getMotivo().esAceptablePor(encargadoProcesador);
    }

    private Prontuario crearProntuario(Excusa excusa) {
        return new Prontuario(
                excusa.getEmpleado(),
                excusa,
                excusa.getEmpleado().getLegajo()
        );
    }

    public void agregarProntuario(Prontuario prontuario) {
        this.prontuarios.add(prontuario);
        System.out.println("Prontuario creado para empleado: " + prontuario.getEmpleado().getNombre());
        this.notificarObservadores(prontuario);
    }

    public List<Prontuario> getProntuarios() {
        return new ArrayList<>(this.prontuarios);
    }

    public void limpiarProntuarios() {
        this.prontuarios.clear();
    }
}
