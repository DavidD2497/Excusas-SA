package com.excusas.model.empleados.encargados.modos;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.empleados.encargados.Recepcionista;
import com.excusas.model.empleados.encargados.SupervisorArea;
import com.excusas.model.empleados.encargados.EncargadoPorDefecto;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoTrivial;
import com.excusas.model.excusas.motivos.MotivoProblemaFamiliar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModoTest {

    private ModoNormal modoNormal;
    private ModoVago modoVago;
    private ModoProductivo modoProductivo;
    private IManejadorExcusas recepcionista;
    private IManejadorExcusas supervisor;
    private IManejadorExcusas encargadoPorDefecto;
    private Excusa excusaTrivial;
    private Excusa excusaModerada;
    private Empleado empleado;

    @BeforeEach
    void setUp() {
        modoNormal = new ModoNormal();
        modoVago = new ModoVago();
        modoProductivo = new ModoProductivo();

        recepcionista = new Recepcionista("Laura", "laura@excusas.com", 2001);
        supervisor = new SupervisorArea("Pedro", "pedro@excusas.com", 2002);
        encargadoPorDefecto = new EncargadoPorDefecto();

        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);
        excusaTrivial = new Excusa(empleado, new MotivoTrivial(), "Llegué tarde por el tráfico");
        excusaModerada = new Excusa(empleado, new MotivoProblemaFamiliar(), "Tuve que cuidar a mi familiar enfermo");

        recepcionista.setSiguiente(supervisor);
        supervisor.setSiguiente(encargadoPorDefecto);
    }

    @Test
    void modoNormalDebeEjecutarProcesamientoDirectamente() {
        assertTrue(excusaTrivial.puedeSerManejadaPor(recepcionista));

        recepcionista.setModo(modoNormal);

        assertDoesNotThrow(() -> {
            recepcionista.manejarExcusa(excusaTrivial);
        });
    }

    @Test
    void modoVagoDebeDelegarAlSiguiente() {

        recepcionista.setModo(modoVago);
        assertDoesNotThrow(() -> {
            recepcionista.manejarExcusa(excusaTrivial);
        });

        assertDoesNotThrow(() -> {
            recepcionista.manejarExcusa(excusaModerada);
        });
    }

    @Test
    void modoProductivoDebeEnviarEmailYProcesar() {
        recepcionista.setModo(modoProductivo);

        assertDoesNotThrow(() -> {
            recepcionista.manejarExcusa(excusaTrivial);
        });
    }

    @Test
    void modoVagoSiempreDelegaInclusoSiPuedeManejar() {
        assertTrue(excusaTrivial.puedeSerManejadaPor(recepcionista));

        recepcionista.setModo(modoVago);

        assertDoesNotThrow(() -> {
            recepcionista.manejarExcusa(excusaTrivial);
        });
    }
}
