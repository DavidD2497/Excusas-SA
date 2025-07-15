package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.service.ExcusaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

import java.util.List;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.exception.ExcusaNotFoundException;

@RestController
@RequestMapping("/excusas")
public class ExcusaController {

    @Autowired
    private ExcusaService excusaService;

    @PostMapping
    public ExcusaResponse crearExcusa(@Valid @RequestBody ExcusaRequest request) {
        Excusa excusa = excusaService.crearExcusa(
                request.getLegajoEmpleado(),
                request.getTipoMotivo(),
                request.getDescripcion()
        );
        return convertirAResponse(excusa);
    }

    @PostMapping("/procesar/indice/{index}")
    public ProcesarExcusaResponse procesarExcusa(@PathVariable int index) {
        try {
            List<Excusa> excusas = excusaService.obtenerTodasLasExcusas();

            if (index < 0) {
                throw new InvalidDataException("El índice no puede ser negativo");
            }
            if (index >= excusas.size()) {
                throw new ExcusaNotFoundException("Excusa no encontrada en el índice: " + index);
            }

            Excusa excusa = excusas.get(index);

            excusaService.procesarExcusa(index);

            ProcesarExcusaResponse response = new ProcesarExcusaResponse();
            response.setMensaje("Excusa procesada correctamente");
            response.setIndice(index);
            response.setEmpleado(excusa.getNombreEmpleado());
            response.setDescripcion(excusa.getDescripcion());
            response.setTipoMotivo(excusa.getMotivo().getClass().getSimpleName());
            return response;
        } catch (InvalidDataException | ExcusaNotFoundException e) {
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Error procesando excusa: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public List<ExcusaResponse> obtenerTodasLasExcusas() {
        return excusaService.obtenerTodasLasExcusas().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Endpoint según consigna exacta
    @GetMapping("/{legajo}")
    public List<ExcusaResponse> obtenerExcusasPorLegajo(@PathVariable int legajo) {
        return excusaService.obtenerExcusasPorEmpleado(legajo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Mantener el endpoint anterior por compatibilidad
    @GetMapping("/buscar/empleado/{legajo}")
    public List<ExcusaResponse> obtenerExcusasPorEmpleado(@PathVariable int legajo) {
        return excusaService.obtenerExcusasPorEmpleado(legajo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/buscar/motivo/{tipoMotivo}")
    public List<ExcusaResponse> obtenerExcusasPorTipoMotivo(@PathVariable String tipoMotivo) {
        return excusaService.obtenerExcusasPorTipoMotivo(tipoMotivo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/rechazadas")
    public List<ExcusaResponse> obtenerExcusasRechazadas() {
        return excusaService.obtenerExcusasRechazadas().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/busqueda")
    public List<ExcusaResponse> buscarExcusas(
            @RequestParam int legajo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return excusaService.buscarExcusasPorLegajoYFechas(legajo, fechaDesde, fechaHasta).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @DeleteMapping("/eliminar")
    public EliminarExcusasResponse eliminarExcusasPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite) {

        if (fechaLimite == null) {
            throw new InvalidDataException("El parámetro fechaLimite es obligatorio para esta operación de eliminación");
        }

        int cantidadEliminada = excusaService.eliminarExcusasAnterioresA(fechaLimite);

        EliminarExcusasResponse response = new EliminarExcusasResponse();
        response.setMensaje("Excusas eliminadas correctamente");
        response.setCantidadEliminada(cantidadEliminada);
        response.setFechaLimite(fechaLimite);
        return response;
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

    public static class EliminarExcusasResponse {
        private String mensaje;
        private int cantidadEliminada;
        private LocalDate fechaLimite;

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public int getCantidadEliminada() { return cantidadEliminada; }
        public void setCantidadEliminada(int cantidadEliminada) { this.cantidadEliminada = cantidadEliminada; }
        public LocalDate getFechaLimite() { return fechaLimite; }
        public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }
    }
}
