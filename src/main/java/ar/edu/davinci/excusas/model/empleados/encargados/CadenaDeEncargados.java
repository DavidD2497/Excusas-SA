package ar.edu.davinci.excusas.model.empleados.encargados;

import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuariosJPA;

public class CadenaDeEncargados {

    private final IManejadorExcusas primerEncargado;

    public CadenaDeEncargados() {
        this.primerEncargado = this.construirCadena();
    }

    public CadenaDeEncargados(AdministradorProntuariosJPA administradorProntuarios) {
        this.primerEncargado = this.construirCadenaConJPA(administradorProntuarios);
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

    private IManejadorExcusas construirCadenaConJPA(AdministradorProntuariosJPA administradorProntuarios) {
        IManejadorExcusas recepcionista = new Recepcionista("Laura Recep", "laura@excusas.com", 2001);
        IManejadorExcusas supervisor = new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002);
        IManejadorExcusas gerente = new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003);
        IManejadorExcusas ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004, administradorProntuarios);
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
