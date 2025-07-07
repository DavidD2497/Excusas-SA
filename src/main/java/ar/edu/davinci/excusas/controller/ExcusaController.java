package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.encargados.CadenaDeEncargados;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/excusas")
public class ExcusaController {

    @Autowired
    private EmpleadoController empleadoController;

    private final List<Excusa> excusas = new ArrayList<>();
    private final CadenaDeEncargados cadenaDeEncargados = new CadenaDeEncargados();

    @PostMapping
    public ExcusaResponse crearExcusa(@RequestBody ExcusaRequest request) {
        Empleado empleado = empleadoController.obtenerEmpleadoPorLegajo(request.getLegajoEmpleado());
        MotivoExcusa motivo = crearMotivo(request.getTipoMotivo());

        Excusa excusa = empleado.crearExcusa(motivo, request.getDescripcion());
        excusas.add(excusa);
        return convertirAResponse(excusa);
    }

    @PostMapping("/{index}/procesar")
    public String procesarExcusa(@PathVariable int index) {
        if (index < 0 || index >= excusas.size()) {
            throw new RuntimeException("Excusa no encontrada");
        }

        Excusa excusa = excusas.get(index);
        cadenaDeEncargados.procesarExcusa(excusa);
        return "Excusa procesada correctamente";
    }

    @GetMapping
    public List<ExcusaResponse> obtenerTodasLasExcusas() {
        return excusas.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/empleado/{legajo}")
    public List<ExcusaResponse> obtenerExcusasPorEmpleado(@PathVariable int legajo) {
        return excusas.stream()
                .filter(excusa -> excusa.getLegajoEmpleado() == legajo)
                .map(this::convertirAResponse)
                .toList();
    }

    private MotivoExcusa crearMotivo(String tipoMotivo) {
        return switch (tipoMotivo.toUpperCase()) {
            case "TRIVIAL" -> new MotivoTrivial();
            case "PROBLEMA_ELECTRICO" -> new MotivoProblemaElectrico();
            case "PROBLEMA_FAMILIAR" -> new MotivoProblemaFamiliar();
            case "COMPLEJO" -> new MotivoComplejo();
            case "INVEROSIMIL" -> new MotivoInverosimil();
            default -> throw new RuntimeException("Tipo de motivo no válido: " + tipoMotivo);
        };
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
        private int legajoEmpleado;
        private String tipoMotivo;
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
}
