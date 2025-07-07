package com.excusas.model.excusas;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.empleados.encargados.*;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.motivos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExcusaTest {

    private Empleado empleado;
    private IManejadorExcusas recepcionista;
    private IManejadorExcusas supervisor;
    private IManejadorExcusas gerente;
    private IManejadorExcusas ceo;
    private IManejadorExcusas encargadoPorDefecto;

    @BeforeEach
    void setUp() {
        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
        recepcionista = new Recepcionista("Laura Recep", "laura@excusas.com", 2001);
        supervisor = new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002);
        gerente = new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003);
        ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004);
        encargadoPorDefecto = new EncargadoPorDefecto();
    }

    @Test
    void excusaTrivialPuedeSerManejadaPorRecepcionista() {
        Excusa excusa = new Excusa(empleado, new MotivoTrivial(), "Llegué tarde por el tráfico");

        assertTrue(excusa.puedeSerManejadaPor(recepcionista));
        assertFalse(excusa.puedeSerManejadaPor(supervisor));
        assertFalse(excusa.puedeSerManejadaPor(gerente));
        assertFalse(excusa.puedeSerManejadaPor(ceo));
        assertFalse(excusa.puedeSerManejadaPor(encargadoPorDefecto));
    }

    @Test
    void excusaModeradaPuedeSerManejadaPorSupervisor() {
        Excusa excusa = new Excusa(empleado, new MotivoProblemaFamiliar(), "Tuve que cuidar a mi familiar enfermo");

        assertFalse(excusa.puedeSerManejadaPor(recepcionista));
        assertTrue(excusa.puedeSerManejadaPor(supervisor));
        assertFalse(excusa.puedeSerManejadaPor(gerente));
        assertFalse(excusa.puedeSerManejadaPor(ceo));
        assertFalse(excusa.puedeSerManejadaPor(encargadoPorDefecto));
    }

    @Test
    void excusaComplejaPuedeSerManejadaPorGerente() {
        Excusa excusa = new Excusa(empleado, new MotivoComplejo(), "Una paloma robó mi bicicleta");

        assertFalse(excusa.puedeSerManejadaPor(recepcionista));
        assertFalse(excusa.puedeSerManejadaPor(supervisor));
        assertTrue(excusa.puedeSerManejadaPor(gerente));
        assertFalse(excusa.puedeSerManejadaPor(ceo));
        assertFalse(excusa.puedeSerManejadaPor(encargadoPorDefecto));
    }

    @Test
    void excusaInverosimilPuedeSerManejadaPorCEO() {
        Excusa excusa = new Excusa(empleado, new MotivoInverosimil(), "Fui abducido por aliens");

        assertFalse(excusa.puedeSerManejadaPor(recepcionista));
        assertFalse(excusa.puedeSerManejadaPor(supervisor));
        assertFalse(excusa.puedeSerManejadaPor(gerente));
        assertTrue(excusa.puedeSerManejadaPor(ceo));
        assertFalse(excusa.puedeSerManejadaPor(encargadoPorDefecto));
    }

    @Test
    void excusaDevuelveDatosCorrectos() {
        MotivoExcusa motivo = new MotivoTrivial();
        String descripcion = "Llegué tarde por el tráfico";
        Excusa excusa = new Excusa(empleado, motivo, descripcion);

        assertEquals(empleado, excusa.getEmpleado());
        assertEquals(motivo, excusa.getMotivo());
        assertEquals(descripcion, excusa.getDescripcion());
        assertEquals("Juan Pérez", excusa.getNombreEmpleado());
        assertEquals("juan@empresa.com", excusa.getEmailEmpleado());
        assertEquals(1001, excusa.getLegajoEmpleado());
    }
}

