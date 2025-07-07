package com.excusas.model.email;

import com.excusas.model.email.interfaces.IEmailSender;

public class EmailSenderConcreto implements IEmailSender {

    public EmailSenderConcreto() {
    }

    @Override
    public void enviarEmail(String unEmailDestino, String unEmailOrigen, String unAsunto, String unCuerpo) {
        System.out.println("=== EMAIL ENVIADO ===");
        System.out.println("Para: " + unEmailDestino);
        System.out.println("De: " + unEmailOrigen);
        System.out.println("Asunto: " + unAsunto);
        System.out.println("Cuerpo: " + unCuerpo);
        System.out.println("====================");
    }
}
