package com.excusas.model.empleados.encargados;

import com.excusas.model.email.EmailSenderConcreto;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.Encargado;

public class Recepcionista extends Encargado {

    public Recepcionista(String nombre, String email, int legajo) {
        super(nombre, email, legajo);
    }

    @Override
    public boolean puedeManejarTrivial() {
        return true;
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        new EmailSenderConcreto().enviarEmail(
                excusa.getEmailEmpleado(),
                this.getEmail(),
                "motivo demora",
                "la licencia fue aceptada"
        );
    }
}
