package ar.edu.davinci.excusas.exception;

public class EmpleadoNotFoundException extends RuntimeException {
    public EmpleadoNotFoundException(String message) {
        super(message);
    }

    public EmpleadoNotFoundException(int legajo) {
        super("Empleado no encontrado con legajo: " + legajo);
    }
}
