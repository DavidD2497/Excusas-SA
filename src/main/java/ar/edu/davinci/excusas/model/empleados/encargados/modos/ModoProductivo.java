package com.excusas.model.empleados.encargados.modos;

import com.excusas.model.email.EmailSenderConcreto;
import com.excusas.model.empleados.interfaces.IManejadorExcusas;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.encargados.modos.interfaces.IModoManejo;

public class ModoProductivo implements IModoManejo {

    @Override
    public void manejar(IManejadorExcusas encargado, Excusa excusa) {
        String emailOrigen = encargado.getEmailOrigen();

        new EmailSenderConcreto().enviarEmail(
                "cto@excusas.com",
                emailOrigen,
                "Procesamiento productivo",
                "Procesando excusa de manera productiva para: " + excusa.getEmpleado().getNombre()
        );
        encargado.ejecutarProcesamiento(excusa);
    }
}
