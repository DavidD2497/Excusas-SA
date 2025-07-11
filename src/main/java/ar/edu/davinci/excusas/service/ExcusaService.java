package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.exception.ExcusaNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.encargados.CadenaDeEncargados;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ExcusaService {

    @Autowired
    private EmpleadoService empleadoService;

    private final List<Excusa> excusas = new ArrayList<>();
    private final CadenaDeEncargados cadenaDeEncargados = new CadenaDeEncargados();
    private final List<String> tiposMotivosValidos = Arrays.asList(
            "TRIVIAL", "PROBLEMA_ELECTRICO", "PROBLEMA_FAMILIAR", "COMPLEJO", "INVEROSIMIL"
    );

    public Excusa crearExcusa(int legajoEmpleado, String tipoMotivo, String descripcion) {
        validarDatosExcusa(legajoEmpleado, tipoMotivo, descripcion);

        Empleado empleado = empleadoService.obtenerEmpleadoPorLegajo(legajoEmpleado);

        validarLimiteExcusasEmpleado(legajoEmpleado);

        MotivoExcusa motivo = crearMotivo(tipoMotivo);
        Excusa excusa = empleado.crearExcusa(motivo, descripcion.trim());
        excusas.add(excusa);
        return excusa;
    }

    public void procesarExcusa(int index) {
        // Validation is now done in the controller
        Excusa excusa = excusas.get(index);
        try {
            cadenaDeEncargados.procesarExcusa(excusa);
        } catch (Exception e) {
            throw new RuntimeException("Error en el procesamiento de la excusa: " + e.getMessage(), e);
        }
    }

    public List<Excusa> obtenerTodasLasExcusas() {
        return new ArrayList<>(excusas);
    }

    public List<Excusa> obtenerExcusasPorEmpleado(int legajo) {
        validarLegajo(legajo);

        // Verificar que el empleado existe
        empleadoService.obtenerEmpleadoPorLegajo(legajo);

        List<Excusa> resultado = excusas.stream()
                .filter(excusa -> excusa.getLegajoEmpleado() == legajo)
                .toList();

        if (resultado.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas para el empleado con legajo: " + legajo);
        }

        return resultado;
    }

    public List<Excusa> obtenerExcusasPorTipoMotivo(String tipoMotivo) {
        validarTipoMotivo(tipoMotivo);

        List<Excusa> resultado = excusas.stream()
                .filter(excusa -> excusa.getMotivo().getClass().getSimpleName().toUpperCase().contains(tipoMotivo.toUpperCase()))
                .toList();

        if (resultado.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas con el tipo de motivo: " + tipoMotivo);
        }

        return resultado;
    }

    private void validarDatosExcusa(int legajoEmpleado, String tipoMotivo, String descripcion) {
        validarLegajo(legajoEmpleado);
        validarTipoMotivo(tipoMotivo);
        validarDescripcion(descripcion);
    }

    private void validarLegajo(int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }
    }

    private void validarTipoMotivo(String tipoMotivo) {
        if (tipoMotivo == null || tipoMotivo.trim().isEmpty()) {
            throw new InvalidDataException("El tipo de motivo es obligatorio");
        }
        if (!tiposMotivosValidos.contains(tipoMotivo.toUpperCase())) {
            throw new InvalidDataException("Tipo de motivo no válido. Tipos válidos: " + tiposMotivosValidos);
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new InvalidDataException("La descripción es obligatoria");
        }
        if (descripcion.trim().length() < 10 || descripcion.trim().length() > 500) {
            throw new InvalidDataException("La descripción debe tener entre 10 y 500 caracteres");
        }
    }

    private void validarLimiteExcusasEmpleado(int legajoEmpleado) {
        long excusasDelEmpleado = excusas.stream()
                .filter(excusa -> excusa.getLegajoEmpleado() == legajoEmpleado)
                .count();

        if (excusasDelEmpleado >= 5) {
            throw new BusinessRuleException("El empleado ya tiene el máximo de 5 excusas registradas");
        }
    }

    private MotivoExcusa crearMotivo(String tipoMotivo) {
        String tipo = tipoMotivo.toUpperCase();

        return switch (tipo) {
            case "TRIVIAL" -> new MotivoTrivial();
            case "PROBLEMA_ELECTRICO" -> new MotivoProblemaElectrico();
            case "PROBLEMA_FAMILIAR" -> new MotivoProblemaFamiliar();
            case "COMPLEJO" -> new MotivoComplejo();
            case "INVEROSIMIL" -> new MotivoInverosimil();
            default -> throw new InvalidDataException("Tipo de motivo no válido: " + tipoMotivo);
        };
    }
}
