package com.excusas.model.empleados.encargados;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoInverosimil;
import com.excusas.model.prontuarios.AdministradorProntuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CEOTest {

    private CEO ceo;
    private Empleado empleado;
    private Excusa excusaInverosimil;

    @BeforeEach
    void setUp() {
        ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004);
        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
        excusaInverosimil = new Excusa(empleado, new MotivoInverosimil(), "Me secuestraron los aliens");
        AdministradorProntuarios.getInstance().limpiarProntuarios();
    }

    @Test
    void deberiaPoderManejarExcusasInverosimiles() {
        assertTrue(ceo.puedeManejarInverosimil());
        assertFalse(ceo.puedeManejarTrivial());
        assertFalse(ceo.puedeManejarModerado());
        assertFalse(ceo.puedeManejarComplejo());
    }

    @Test
    void deberiaProcesarExcusaInverosimilCorrectamente() {
        assertDoesNotThrow(() -> {
            ceo.procesarExcusa(excusaInverosimil);
        });
    }

    @Test
    void deberiaTenerDatosCorrectosDelEmpleado() {
        assertEquals("Roberto CEO", ceo.getNombre());
        assertEquals("roberto@excusas.com", ceo.getEmail());
        assertEquals(2004, ceo.getLegajo());
    }

    @Test
    void deberiaEstarRegistradoComoObservadorEnAdministradorProntuarios() {
        assertDoesNotThrow(() -> {
            ceo.manejarExcusa(excusaInverosimil);
        });
    }
}
