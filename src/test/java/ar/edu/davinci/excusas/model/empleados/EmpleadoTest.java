package com.excusas.model.empleados;

import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmpleadoTest {

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
    }

    @Test
    void empleadoPuedeCrearExcusaConMotivoTrivial() {
        MotivoExcusa motivo = new MotivoTrivial();
        String descripcion = "Llegué tarde por el tráfico";

        Excusa excusa = empleado.crearExcusa(motivo, descripcion);

        assertNotNull(excusa);
        assertEquals(empleado, excusa.getEmpleado());
        assertEquals(motivo, excusa.getMotivo());
        assertEquals(descripcion, excusa.getDescripcion());
    }

    @Test
    void empleadoPuedeCrearExcusaConMotivoModerado() {
        MotivoExcusa motivo = new MotivoProblemaFamiliar();
        String descripcion = "Tuve que cuidar a mi familiar enfermo";

        Excusa excusa = empleado.crearExcusa(motivo, descripcion);

        assertNotNull(excusa);
        assertEquals(empleado, excusa.getEmpleado());
        assertEquals(motivo, excusa.getMotivo());
        assertEquals(descripcion, excusa.getDescripcion());
    }

    @Test
    void empleadoPuedeCrearExcusaConMotivoComplejo() {
        MotivoExcusa motivo = new MotivoComplejo();
        String descripcion = "Una paloma robó mi bicicleta";

        Excusa excusa = empleado.crearExcusa(motivo, descripcion);

        assertNotNull(excusa);
        assertEquals(empleado, excusa.getEmpleado());
        assertEquals(motivo, excusa.getMotivo());
        assertEquals(descripcion, excusa.getDescripcion());
    }

    @Test
    void empleadoPuedeCrearExcusaConMotivoInverosimil() {
        MotivoExcusa motivo = new MotivoInverosimil();
        String descripcion = "Fui abducido por aliens";

        Excusa excusa = empleado.crearExcusa(motivo, descripcion);

        assertNotNull(excusa);
        assertEquals(empleado, excusa.getEmpleado());
        assertEquals(motivo, excusa.getMotivo());
        assertEquals(descripcion, excusa.getDescripcion());
    }

    @Test
    void empleadoTieneDatosCorrectos() {
        assertEquals("Juan Pérez", empleado.getNombre());
        assertEquals("juan@empresa.com", empleado.getEmail());
        assertEquals(1001, empleado.getLegajo());
    }
}

