package ar.edu.davinci.excusas.exception;

public class ExcusaNotFoundException extends RuntimeException {
    public ExcusaNotFoundException(String message) {
        super(message);
    }

    public ExcusaNotFoundException(int index) {
        super("Excusa no encontrada en el índice: " + index);
    }
}
