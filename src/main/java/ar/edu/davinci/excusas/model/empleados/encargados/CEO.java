package ar.edu.davinci.excusas.model.empleados.encargados;

import ar.edu.davinci.excusas.model.prontuarios.interfaces.IObserver;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import ar.edu.davinci.excusas.model.email.EmailSenderConcreto;
import ar.edu.davinci.excusas.model.empleados.Encargado;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuarios;

public class CEO extends Encargado implements IObserver {

    public CEO(String nombre, String email, int legajo) {
        super(nombre, email, legajo);
        AdministradorProntuarios.getInstance().agregarObservador(this);
    }

    @Override
    public boolean puedeManejarInverosimil() {
        return true;
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        new EmailSenderConcreto().enviarEmail(
                excusa.getEmailEmpleado(),
                this.getEmail(),
                "Respuesta CEO",
                "Aprobado por creatividad"
        );

        AdministradorProntuarios.getInstance().notificarExcusaProcesada(excusa, this);
    }

    @Override
    public void actualizar(Prontuario prontuario) {
        System.out.println("CEO " + this.getNombre() + " notificado sobre nuevo prontuario de: " +
                prontuario.getEmpleado().getNombre());

        this.notificarOtrosCEOs(prontuario);
    }

    private void notificarOtrosCEOs(Prontuario prontuario) {
        new EmailSenderConcreto().enviarEmail(
                "todos-ceos@excusas.com",
                this.getEmail(),
                "Nuevo Prontuario Creado",
                "Se ha creado un nuevo prontuario para el empleado: " +
                        prontuario.getEmpleado().getNombre() + " (Legajo: " + prontuario.getLegajo() + ")"
        );
        System.out.println("Notificando a todos los CEOs sobre el nuevo prontuario");
    }
}
