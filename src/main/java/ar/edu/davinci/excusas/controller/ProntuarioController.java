package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuarios;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private final AdministradorProntuarios administrador = AdministradorProntuarios.getInstance();

    @GetMapping
    public List<ProntuarioResponse> obtenerTodosLosProntuarios() {
        return administrador.getProntuarios().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/empleado/{legajo}")
    public List<ProntuarioResponse> obtenerProntuariosPorEmpleado(@PathVariable int legajo) {
        return administrador.getProntuarios().stream()
                .filter(prontuario -> prontuario.getLegajo() == legajo)
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/count")
    public int contarProntuarios() {
        return administrador.getProntuarios().size();
    }

    @DeleteMapping
    public String limpiarProntuarios() {
        administrador.limpiarProntuarios();
        return "Todos los prontuarios han sido eliminados";
    }

    private ProntuarioResponse convertirAResponse(Prontuario prontuario) {
        ProntuarioResponse response = new ProntuarioResponse();
        response.setNombreEmpleado(prontuario.getEmpleado().getNombre());
        response.setEmailEmpleado(prontuario.getEmpleado().getEmail());
        response.setLegajo(prontuario.getLegajo());
        response.setDescripcionExcusa(prontuario.getExcusa().getDescripcion());
        response.setTipoMotivoExcusa(prontuario.getExcusa().getMotivo().getClass().getSimpleName());
        return response;
    }

    public static class ProntuarioResponse {
        private String nombreEmpleado;
        private String emailEmpleado;
        private int legajo;
        private String descripcionExcusa;
        private String tipoMotivoExcusa;

        public String getNombreEmpleado() { return nombreEmpleado; }
        public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
        public String getEmailEmpleado() { return emailEmpleado; }
        public void setEmailEmpleado(String emailEmpleado) { this.emailEmpleado = emailEmpleado; }
        public int getLegajo() { return legajo; }
        public void setLegajo(int legajo) { this.legajo = legajo; }
        public String getDescripcionExcusa() { return descripcionExcusa; }
        public void setDescripcionExcusa(String descripcionExcusa) { this.descripcionExcusa = descripcionExcusa; }
        public String getTipoMotivoExcusa() { return tipoMotivoExcusa; }
        public void setTipoMotivoExcusa(String tipoMotivoExcusa) { this.tipoMotivoExcusa = tipoMotivoExcusa; }
    }
}

