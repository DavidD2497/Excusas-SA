package com.excusas.model.excusas.motivos;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.empleados.encargados.*;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.Excusa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotivoTest {

    private MotivoTrivial motivoTrivial;
    private MotivoProblemaFamiliar motivoProblemaFamiliar;
    private MotivoProblemaElectrico motivoProblemaElectrico;
    private MotivoComplejo motivoComplejo;
    private MotivoInverosimil motivoInverosimil;

    private IManejadorExcusas recepcionista;
    private IManejadorExcusas supervisor;
    private IManejadorExcusas gerente;
    private IManejadorExcusas ceo;
    private IManejadorExcusas encargadoPorDefecto;

    private Empleado empleado;
    private Excusa excusaProblemaElectrico;
    private Excusa excusaProblemaFamiliar;

    @BeforeEach
    void setUp() {
        motivoTrivial = new MotivoTrivial();
        motivoProblemaFamiliar = new MotivoProblemaFamiliar();
        motivoProblemaElectrico = new MotivoProblemaElectrico();
        motivoComplejo = new MotivoComplejo();
        motivoInverosimil = new MotivoInverosimil();

        recepcionista = new Recepcionista("Laura", "laura@excusas.com", 2001);
        supervisor = new SupervisorArea("Pedro", "pedro@excusas.com", 2002);
        gerente = new GerenteRecursosHumanos("Sofia", "sofia@excusas.com", 2003);
        ceo = new CEO("Roberto", "roberto@excusas.com", 2004);
        encargadoPorDefecto = new EncargadoPorDefecto();

        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
        excusaProblemaElectrico = new Excusa(empleado, motivoProblemaElectrico, "Se cortó la luz en todo el barrio");
        excusaProblemaFamiliar = new Excusa(empleado, motivoProblemaFamiliar, "Tuve que cuidar a mi familiar enfermo");
    }

    @Test
    void motivoTrivialDebeSerAceptadoPorRecepcionista() {
        assertTrue(motivoTrivial.esAceptablePor(recepcionista));
        assertFalse(motivoTrivial.esAceptablePor(supervisor));
        assertFalse(motivoTrivial.esAceptablePor(gerente));
        assertFalse(motivoTrivial.esAceptablePor(ceo));
        assertFalse(motivoTrivial.esAceptablePor(encargadoPorDefecto));
    }

    @Test
    void motivoProblemaFamiliarDebeSerAceptadoPorSupervisor() {
        assertFalse(motivoProblemaFamiliar.esAceptablePor(recepcionista));
        assertTrue(motivoProblemaFamiliar.esAceptablePor(supervisor));
        assertFalse(motivoProblemaFamiliar.esAceptablePor(gerente));
        assertFalse(motivoProblemaFamiliar.esAceptablePor(ceo));
        assertFalse(motivoProblemaFamiliar.esAceptablePor(encargadoPorDefecto));
    }

    @Test
    void motivoProblemaElectricoDebeSerAceptadoPorSupervisor() {
        assertFalse(motivoProblemaElectrico.esAceptablePor(recepcionista));
        assertTrue(motivoProblemaElectrico.esAceptablePor(supervisor));
        assertFalse(motivoProblemaElectrico.esAceptablePor(gerente));
        assertFalse(motivoProblemaElectrico.esAceptablePor(ceo));
        assertFalse(motivoProblemaElectrico.esAceptablePor(encargadoPorDefecto));
    }

    @Test
    void motivoComplejoDebeSerAceptadoPorGerente() {
        assertFalse(motivoComplejo.esAceptablePor(recepcionista));
        assertFalse(motivoComplejo.esAceptablePor(supervisor));
        assertTrue(motivoComplejo.esAceptablePor(gerente));
        assertFalse(motivoComplejo.esAceptablePor(ceo));
        assertFalse(motivoComplejo.esAceptablePor(encargadoPorDefecto));
    }

    @Test
    void motivoInverosimilDebeSerAceptadoPorCEO() {
        assertFalse(motivoInverosimil.esAceptablePor(recepcionista));
        assertFalse(motivoInverosimil.esAceptablePor(supervisor));
        assertFalse(motivoInverosimil.esAceptablePor(gerente));
        assertTrue(motivoInverosimil.esAceptablePor(ceo));
        assertFalse(motivoInverosimil.esAceptablePor(encargadoPorDefecto));
    }

    @Test
    void motivosModeradosDebenImplementarDoubleDispatch() {
        SupervisorArea supervisorConcreto = (SupervisorArea) supervisor;
        assertDoesNotThrow(() -> {
            motivoProblemaElectrico.procesarConSupervisor(supervisorConcreto, excusaProblemaElectrico);
            motivoProblemaFamiliar.procesarConSupervisor(supervisorConcreto, excusaProblemaFamiliar);
        });
    }
}
