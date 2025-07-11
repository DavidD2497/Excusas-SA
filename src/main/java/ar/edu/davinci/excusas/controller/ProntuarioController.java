package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuarios;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final AdministradorProntuarios administrador = AdministradorProntuarios.getInstance();

    @GetMapping
    public List<ProntuarioResponse> obtenerTodosLosProntuarios() {
        return administrador.getProntuarios().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/buscar/empleado/{legajo}")
    public List<ProntuarioResponse> obtenerProntuariosPorEmpleado(@PathVariable int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }

        List<ProntuarioResponse> prontuarios = administrador.getProntuarios().stream()
                .filter(prontuario -> prontuario.getLegajo() == legajo)
                .map(this::convertirAResponse)
                .toList();

        if (prontuarios.isEmpty()) {
            throw new BusinessRuleException("No se encontraron prontuarios para el empleado con legajo: " + legajo);
        }

        return prontuarios;
    }

    @GetMapping("/estadisticas/count")
    public ContarProntuariosResponse contarProntuarios() {
        int cantidad = administrador.getProntuarios().size();
        ContarProntuariosResponse response = new ContarProntuariosResponse();
        response.setCantidad(cantidad);
        response.setMensaje("Total de prontuarios: " + cantidad);
        return response;
    }

    @DeleteMapping("/administracion/limpiar")
    public LimpiarProntuariosResponse limpiarProntuarios() {
        int cantidadAnterior = administrador.getProntuarios().size();

        if (cantidadAnterior == 0) {
            throw new BusinessRuleException("No hay prontuarios para eliminar");
        }

        administrador.limpiarProntuarios();

        LimpiarProntuariosResponse response = new LimpiarProntuariosResponse();
        response.setMensaje("Todos los prontuarios han sido eliminados");
        response.setCantidadEliminada(cantidadAnterior);
        response.setCantidadActual(0);
        return response;
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

    public static class ContarProntuariosResponse {
        private int cantidad;
        private String mensaje;

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    public static class LimpiarProntuariosResponse {
        private String mensaje;
        private int cantidadEliminada;
        private int cantidadActual;

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public int getCantidadEliminada() { return cantidadEliminada; }
        public void setCantidadEliminada(int cantidadEliminada) { this.cantidadEliminada = cantidadEliminada; }
        public int getCantidadActual() { return cantidadActual; }
        public void setCantidadActual(int cantidadActual) { this.cantidadActual = cantidadActual; }
    }
}
