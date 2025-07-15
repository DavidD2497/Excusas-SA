package ar.edu.davinci.excusas.model.empleados.encargados;

import ar.edu.davinci.excusas.model.empleados.Encargado;
import ar.edu.davinci.excusas.model.excusas.Excusa;

import java.util.List;

public class EncargadoDinamico extends Encargado {

    private final List<String> capacidades;

    public EncargadoDinamico(String nombre, String email, int legajo, List<String> capacidades) {
        super(nombre, email, legajo);
        this.capacidades = capacidades.stream()
                .map(String::toUpperCase)
                .toList();
    }

    @Override
    public boolean puedeManejarTrivial() {
        return capacidades.contains("TRIVIAL");
    }

    @Override
    public boolean puedeManejarModerado() {
        return capacidades.contains("MODERADO");
    }

    @Override
    public boolean puedeManejarComplejo() {
        return capacidades.contains("COMPLEJO");
    }

    @Override
    public boolean puedeManejarInverosimil() {
        return capacidades.contains("INVEROSIMIL");
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        System.out.println("Encargado dinámico " + this.getNombre() + " procesando excusa: " + excusa.getDescripcion());

        // Lógica de procesamiento genérica
        String tipoMotivo = excusa.getMotivo().getClass().getSimpleName();
        String mensaje = "Excusa " + tipoMotivo.toLowerCase() + " procesada por encargado dinámico";

        System.out.println(mensaje + " para empleado: " + excusa.getNombreEmpleado());
    }

    public List<String> getCapacidades() {
        return capacidades;
    }
}

