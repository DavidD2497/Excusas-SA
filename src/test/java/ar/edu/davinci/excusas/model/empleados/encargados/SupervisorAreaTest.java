package com.excusas.model.empleados.encargados;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoProblemaElectrico;
import com.excusas.model.excusas.motivos.MotivoProblemaFamiliar;
import com.excusas.model.email.EmailSenderConcreto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupervisorAreaTest {

    private SupervisorArea supervisor;
    private Empleado empleado;
    private EmailSenderConcreto emailSenderMock;

    @BeforeEach
    void setUp() {
        supervisor = new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002);
        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
    }

    @Test
    void supervisorDebePoderManejarExcusasModeradas() {
        assertTrue(supervisor.puedeManejarModerado());
        assertFalse(supervisor.puedeManejarTrivial());
        assertFalse(supervisor.puedeManejarComplejo());
        assertFalse(supervisor.puedeManejarInverosimil());
    }

    @Test
    void supervisorDebeProcesarProblemaElectrico() {
        Excusa excusa = new Excusa(empleado, new MotivoProblemaElectrico(), "Se cortó la luz en todo el barrio");

        assertTrue(excusa.puedeSerManejadaPor(supervisor));

        assertDoesNotThrow(() -> {
            supervisor.procesarExcusa(excusa);
        });
    }

    @Test
    void supervisorDebeProcesarProblemaFamiliar() {
        Excusa excusa = new Excusa(empleado, new MotivoProblemaFamiliar(), "Tuve que cuidar a mi familiar enfermo");

        assertTrue(excusa.puedeSerManejadaPor(supervisor));

        assertDoesNotThrow(() -> {
            supervisor.procesarExcusa(excusa);
        });
    }

    @Test
    void supervisorDebeTenerDatosCorrectos() {
        assertEquals("Pedro Super", supervisor.getNombre());
        assertEquals("pedro@excusas.com", supervisor.getEmail());
        assertEquals(2002, supervisor.getLegajo());
    }
}

