package com.excusas.model.empleados.encargados;

import com.excusas.model.empleados.Empleado;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.*;
import com.excusas.model.email.EmailSenderConcreto;
import com.excusas.model.email.interfaces.IEmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadenaDeEncargadosTest {

    private CadenaDeEncargados cadena;
    private Empleado empleado;
    private IEmailSender emailSenderMock;

    private Recepcionista recepcionista;
    private SupervisorArea supervisor;
    private GerenteRecursosHumanos gerente;
    private CEO ceo;
    private EncargadoPorDefecto encargadoPorDefecto;

    @BeforeEach
    void setUp() {
        empleado = new Empleado("Juan Pérez", "juan@empresa.com", 1001);

        emailSenderMock = Mockito.mock(IEmailSender.class);

        recepcionista = new Recepcionista("Laura Recep", "laura@excusas.com", 2001);
        supervisor = new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002);
        gerente = new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003);
        ceo = new CEO("Roberto CEO", "roberto@excusas.com", 2004);
        encargadoPorDefecto = new EncargadoPorDefecto();

        recepcionista.setSiguiente(supervisor);
        supervisor.setSiguiente(gerente);
        gerente.setSiguiente(ceo);
        ceo.setSiguiente(encargadoPorDefecto);

        cadena = new CadenaDeEncargados();
    }

    @Test
    void recepcionistaDebeAceptarExcusasTriviales() {
        Excusa excusaTrivial = new Excusa(empleado, new MotivoTrivial(), "Llegué tarde por el tráfico");

        assertTrue(recepcionista.puedeManejarTrivial());
        assertTrue(excusaTrivial.puedeSerManejadaPor(recepcionista));

        recepcionista.manejarExcusa(excusaTrivial);
    }

    @Test
    void supervisorDebeAceptarExcusasModeradas() {
        Excusa excusaModerada = new Excusa(empleado, new MotivoProblemaFamiliar(), "Tuve que cuidar a mi familiar enfermo");

        assertTrue(supervisor.puedeManejarModerado());
        assertTrue(excusaModerada.puedeSerManejadaPor(supervisor));

        supervisor.manejarExcusa(excusaModerada);
    }

    @Test
    void gerenteDebeAceptarExcusasComplejas() {
        Excusa excusaCompleja = new Excusa(empleado, new MotivoComplejo(), "Una paloma robó mi bicicleta");

        assertTrue(gerente.puedeManejarComplejo());
        assertTrue(excusaCompleja.puedeSerManejadaPor(gerente));

        gerente.manejarExcusa(excusaCompleja);
    }

    @Test
    void ceoDebeAceptarExcusasInverosimiles() {
        Excusa excusaInverosimil = new Excusa(empleado, new MotivoInverosimil(), "Fui abducido por aliens");

        assertTrue(ceo.puedeManejarInverosimil());
        assertTrue(excusaInverosimil.puedeSerManejadaPor(ceo));

        ceo.manejarExcusa(excusaInverosimil);
    }

    @Test
    void cadenaDebeEnviarExcusaAlEncargadoCorrecto() {
        Excusa excusaTrivial = new Excusa(empleado, new MotivoTrivial(), "Llegué tarde por el tráfico");
        Excusa excusaModerada = new Excusa(empleado, new MotivoProblemaFamiliar(), "Tuve que cuidar a mi familiar enfermo");
        Excusa excusaCompleja = new Excusa(empleado, new MotivoComplejo(), "Una paloma robó mi bicicleta");
        Excusa excusaInverosimil = new Excusa(empleado, new MotivoInverosimil(), "Fui abducido por aliens");

        assertDoesNotThrow(() -> {
            cadena.procesarExcusa(excusaTrivial);
            cadena.procesarExcusa(excusaModerada);
            cadena.procesarExcusa(excusaCompleja);
            cadena.procesarExcusa(excusaInverosimil);
        });
    }
}

