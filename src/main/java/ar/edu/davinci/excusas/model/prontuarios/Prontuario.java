package ar.edu.davinci.excusas.model.prontuarios;

import ar.edu.davinci.excusas.model.prontuarios.interfaces.IProntuario;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.excusas.Excusa;


public class Prontuario implements IProntuario {

    private final Empleado empleado;
    private final Excusa excusa;
    private final int legajo;

    public Prontuario(Empleado empleado, Excusa excusa, int legajo) {
        this.empleado = empleado;
        this.excusa = excusa;
        this.legajo = legajo;
    }

    @Override
    public Empleado getEmpleado() {
        return this.empleado;
    }

    @Override
    public Excusa getExcusa() {
        return this.excusa;
    }

    @Override
    public int getLegajo() {
        return this.legajo;
    }
}
