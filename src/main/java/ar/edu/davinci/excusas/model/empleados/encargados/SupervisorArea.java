package com.excusas.model.empleados.encargados;

import com.excusas.model.email.EmailSenderConcreto;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.empleados.Encargado;

public class SupervisorArea extends Encargado {

    public SupervisorArea(String nombre, String email, int legajo) {
        super(nombre, email, legajo);
    }

    @Override
    public boolean puedeManejarModerado() {
        return true;
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        excusa.getMotivo().procesarConSupervisor(this, excusa);
    }

    public void procesarProblemaElectrico(Excusa excusa) {
        new EmailSenderConcreto().enviarEmail(
                "EDESUR@mailfake.com.ar",
                this.getEmail(),
                "Consulta corte de luz",
                "Consulta si hubo corte de luz en la zona"
        );
        System.out.println("Consultando a EDESUR sobre corte de luz en la zona");
    }

    public void procesarProblemaFamiliar(Excusa excusa) {
        new EmailSenderConcreto().enviarEmail(
                excusa.getEmailEmpleado(),
                this.getEmail(),
                "Consulta familiar",
                "¿Todo está bien con tu familiar?"
        );
        System.out.println("Preguntando al empleado si todo está bien con el familiar");
    }

    public void procesarMotivoModeradoGenerico(Excusa excusa) {
        System.out.println("Supervisor procesando excusa moderada para: " + excusa.getNombreEmpleado());
    }
}
