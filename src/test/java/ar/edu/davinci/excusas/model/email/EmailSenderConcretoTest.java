package com.excusas.model.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailSenderConcretoTest {

    private EmailSenderConcreto emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new EmailSenderConcreto();
    }

    @Test
    void deberiaEnviarEmailCorrectamente() {
        String destino = "destino@empresa.com";
        String origen = "origen@empresa.com";
        String asunto = "Asunto de prueba";
        String cuerpo = "Cuerpo del mensaje";

        assertDoesNotThrow(() -> {
            emailSender.enviarEmail(destino, origen, asunto, cuerpo);
        });
    }
}
