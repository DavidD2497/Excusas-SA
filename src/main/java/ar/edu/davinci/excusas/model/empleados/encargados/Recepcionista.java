package ar.edu.davinci.excusas.model.empleados.encargados;

import ar.edu.davinci.excusas.model.email.EmailSenderConcreto;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.empleados.Encargado;

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
