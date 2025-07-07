package com.excusas.model.prontuarios;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.empleados.encargados.CEO;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoInverosimil;
import com.excusas.model.prontuarios.interfaces.IObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorProntuariosTest {

    private AdministradorProntuarios administrador;
    private Empleado empleado;
    private Excusa excusa;
    private CEO ceo;
    private IObserver observadorMock;

    @BeforeEach
    void setUp() {
        administrador = AdministradorProntuarios.getInstance();
        administrador.limpiarProntuarios();

        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
        excusa = new Excusa(empleado, new MotivoInverosimil(), "Fui abducido por aliens");
        ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004);

        observadorMock = Mockito.mock(IObserver.class);
        administrador.agregarObservador(observadorMock);
    }

    @Test
    void debeCrearProntuarioCuandoCEOProcesaExcusaInverosimil() {
        assertEquals(0, administrador.getProntuarios().size());

        administrador.notificarExcusaProcesada(excusa, ceo);

        assertEquals(1, administrador.getProntuarios().size());

        Prontuario prontuario = administrador.getProntuarios().get(0);
        assertEquals(empleado, prontuario.getEmpleado());
        assertEquals(excusa, prontuario.getExcusa());
        assertEquals(empleado.getLegajo(), prontuario.getLegajo());
    }

    @Test
    void debeNotificarObservadoresCuandoSeAgregaProntuario() {
        Prontuario prontuario = new Prontuario(empleado, excusa, empleado.getLegajo());

        administrador.agregarProntuario(prontuario);

        verify(observadorMock, times(1)).actualizar(prontuario);
    }

    @Test
    void debeLimpiarProntuariosCorrectamente() {
        Prontuario prontuario = new Prontuario(empleado, excusa, empleado.getLegajo());
        administrador.agregarProntuario(prontuario);

        assertEquals(1, administrador.getProntuarios().size());

        administrador.limpiarProntuarios();

        assertEquals(0, administrador.getProntuarios().size());
    }

    @Test
    void noDebeCrearProntuarioCuandoNoEsCEO() {
        Empleado noEsCeo = new Empleado("No CEO", "noceo@excusas.com", 9999);

        assertEquals(0, administrador.getProntuarios().size());
    }
}

