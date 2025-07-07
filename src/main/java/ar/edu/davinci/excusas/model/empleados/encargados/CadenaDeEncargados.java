package com.excusas.model.empleados.encargados;

import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;

public class CadenaDeEncargados {

    private final IManejadorExcusas primerEncargado;

    public CadenaDeEncargados() {
        this.primerEncargado = this.construirCadena();
    }

    private IManejadorExcusas construirCadena() {

        IManejadorExcusas recepcionista = new Recepcionista("Laura Recep", "laura@excusas.com", 2001);
        IManejadorExcusas supervisor = new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002);
        IManejadorExcusas gerente = new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003);
        IManejadorExcusas ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004);
        IManejadorExcusas encargadoDefecto = new EncargadoPorDefecto();

        recepcionista.setSiguiente(supervisor);
        supervisor.setSiguiente(gerente);
        gerente.setSiguiente(ceo);
        ceo.setSiguiente(encargadoDefecto);

        return recepcionista;
    }

    public void procesarExcusa(Excusa excusa) {
        this.primerEncargado.manejarExcusa(excusa);
    }
}
