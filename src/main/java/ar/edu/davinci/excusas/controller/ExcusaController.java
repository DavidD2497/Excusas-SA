package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.exception.ExcusaNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.encargados.CadenaDeEncargados;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/excusas")
public class ExcusaController {

    @Autowired
    private EmpleadoController empleadoController;

    private final List<Excusa> excusas = new ArrayList<>();
    private final CadenaDeEncargados cadenaDeEncargados = new CadenaDeEncargados();
    private final List<String> tiposMotivosValidos = Arrays.asList(
            "TRIVIAL", "PROBLEMA_ELECTRICO", "PROBLEMA_FAMILIAR", "COMPLEJO", "INVEROSIMIL"
    );

    @PostMapping
    public ExcusaResponse crearExcusa(@Valid @RequestBody ExcusaRequest request) {

        if (!tiposMotivosValidos.contains(request.getTipoMotivo().toUpperCase())) {
            throw new InvalidDataException("Tipo de motivo no válido. Tipos válidos: " + tiposMotivosValidos);
        }

        Empleado empleado = empleadoController.obtenerEmpleadoPorLegajo(request.getLegajoEmpleado());

        long excusasDelEmpleado = excusas.stream()
                .filter(excusa -> excusa.getLegajoEmpleado() == request.getLegajoEmpleado())
                .count();

        if (excusasDelEmpleado >= 5) {
            throw new BusinessRuleException("El empleado ya tiene el máximo de 5 excusas registradas");
        }

        MotivoExcusa motivo = crearMotivo(request.getTipoMotivo());
        Excusa excusa = empleado.crearExcusa(motivo, request.getDescripcion().trim());
        excusas.add(excusa);
        return convertirAResponse(excusa);
    }

    @PostMapping("/procesar/indice/{index}")
    public ProcesarExcusaResponse procesarExcusa(@PathVariable int index) {
        if (index < 0) {
            throw new InvalidDataException("El índice no puede ser negativo");
        }

        if (index >= excusas.size()) {
            throw new ExcusaNotFoundException("Excusa no encontrada en el índice: " + index + ". Total de excusas: " + excusas.size());
        }

        Excusa excusa = excusas.get(index);
        cadenaDeEncargados.procesarExcusa(excusa);

        ProcesarExcusaResponse response = new ProcesarExcusaResponse();
        response.setMensaje("Excusa procesada correctamente");
        response.setIndice(index);
        response.setEmpleado(excusa.getNombreEmpleado());
        response.setDescripcion(excusa.getDescripcion());
        response.setTipoMotivo(excusa.getMotivo().getClass().getSimpleName());
        return response;
    }

    @GetMapping
    public List<ExcusaResponse> obtenerTodasLasExcusas() {
        return excusas.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/buscar/empleado/{legajo}")
    public List<ExcusaResponse> obtenerExcusasPorEmpleado(@PathVariable int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }

        empleadoController.obtenerEmpleadoPorLegajo(legajo);

        List<ExcusaResponse> resultado = excusas.stream()
                .filter(excusa -> excusa.getLegajoEmpleado() == legajo)
                .map(this::convertirAResponse)
                .toList();

        if (resultado.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas para el empleado con legajo: " + legajo);
        }

        return resultado;
    }

    @GetMapping("/buscar/motivo/{tipoMotivo}")
    public List<ExcusaResponse> obtenerExcusasPorTipoMotivo(@PathVariable String tipoMotivo) {
        if (!tiposMotivosValidos.contains(tipoMotivo.toUpperCase())) {
            throw new InvalidDataException("Tipo de motivo no válido. Tipos válidos: " + tiposMotivosValidos);
        }

        List<ExcusaResponse> resultado = excusas.stream()
                .filter(excusa -> excusa.getMotivo().getClass().getSimpleName().toUpperCase().contains(tipoMotivo.toUpperCase()))
                .map(this::convertirAResponse)
                .toList();

        if (resultado.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas con el tipo de motivo: " + tipoMotivo);
        }

        return resultado;
    }

    private MotivoExcusa crearMotivo(String tipoMotivo) {
        String tipo = tipoMotivo.toUpperCase();

        if (tipo.equals("TRIVIAL")) {
            return new MotivoTrivial();
        } else if (tipo.equals("PROBLEMA_ELECTRICO")) {
            return new MotivoProblemaElectrico();
        } else if (tipo.equals("PROBLEMA_FAMILIAR")) {
            return new MotivoProblemaFamiliar();
        } else if (tipo.equals("COMPLEJO")) {
            return new MotivoComplejo();
        } else if (tipo.equals("INVEROSIMIL")) {
            return new MotivoInverosimil();
        } else {
            throw new InvalidDataException("Tipo de motivo no válido: " + tipoMotivo);
        }
    }

    private ExcusaResponse convertirAResponse(Excusa excusa) {
        ExcusaResponse response = new ExcusaResponse();
        response.setNombreEmpleado(excusa.getNombreEmpleado());
        response.setEmailEmpleado(excusa.getEmailEmpleado());
        response.setLegajoEmpleado(excusa.getLegajoEmpleado());
        response.setDescripcion(excusa.getDescripcion());
        response.setTipoMotivo(excusa.getMotivo().getClass().getSimpleName());
        return response;
    }

    public static class ExcusaRequest {
        @NotNull(message = "El legajo del empleado es obligatorio")
        @Min(value = 1001, message = "El legajo debe ser mayor a 1000")
        private int legajoEmpleado;

        @NotBlank(message = "El tipo de motivo es obligatorio")
        private String tipoMotivo;

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        private String descripcion;

        public int getLegajoEmpleado() { return legajoEmpleado; }
        public void setLegajoEmpleado(int legajoEmpleado) { this.legajoEmpleado = legajoEmpleado; }
        public String getTipoMotivo() { return tipoMotivo; }
        public void setTipoMotivo(String tipoMotivo) { this.tipoMotivo = tipoMotivo; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public static class ExcusaResponse {
        private String nombreEmpleado;
        private String emailEmpleado;
        private int legajoEmpleado;
        private String descripcion;
        private String tipoMotivo;

        public String getNombreEmpleado() { return nombreEmpleado; }
        public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
        public String getEmailEmpleado() { return emailEmpleado; }
        public void setEmailEmpleado(String emailEmpleado) { this.emailEmpleado = emailEmpleado; }
        public int getLegajoEmpleado() { return legajoEmpleado; }
        public void setLegajoEmpleado(int legajoEmpleado) { this.legajoEmpleado = legajoEmpleado; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getTipoMotivo() { return tipoMotivo; }
        public void setTipoMotivo(String tipoMotivo) { this.tipoMotivo = tipoMotivo; }
    }

    public static class ProcesarExcusaResponse {
        private String mensaje;
        private int indice;
        private String empleado;
        private String descripcion;
        private String tipoMotivo;

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public int getIndice() { return indice; }
        public void setIndice(int indice) { this.indice = indice; }
        public String getEmpleado() { return empleado; }
        public void setEmpleado(String empleado) { this.empleado = empleado; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getTipoMotivo() { return tipoMotivo; }
        public void setTipoMotivo(String tipoMotivo) { this.tipoMotivo = tipoMotivo; }
    }
}
