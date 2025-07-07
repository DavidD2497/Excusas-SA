package com.excusas.model.email.interfaces;

public interface IEmailSender {
    void enviarEmail(String unEmailDestino, String unEmailOrigen, String unAsunto, String unCuerpo);
}

