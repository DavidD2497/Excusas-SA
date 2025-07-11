package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import ar.edu.davinci.excusas.service.ProntuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @GetMapping
    public List<ProntuarioResponse> obtenerTodosLosProntuarios() {
        return prontuarioService.obtenerTodosLosProntuarios().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/buscar/empleado/{legajo}")
    public List<ProntuarioResponse> obtenerProntuariosPorEmpleado(@PathVariable int legajo) {
        return prontuarioService.obtenerProntuariosPorEmpleado(legajo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/estadisticas/count")
    public ContarProntuariosResponse contarProntuarios() {
        int cantidad = prontuarioService.contarProntuarios();
        ContarProntuariosResponse response = new ContarProntuariosResponse();
        response.setCantidad(cantidad);
        response.setMensaje("Total de prontuarios: " + cantidad);
        return response;
    }

    @DeleteMapping("/administracion/limpiar")
    public LimpiarProntuariosResponse limpiarProntuarios() {
        int cantidadEliminada = prontuarioService.limpiarProntuarios();

        LimpiarProntuariosResponse response = new LimpiarProntuariosResponse();
        response.setMensaje("Todos los prontuarios han sido eliminados");
        response.setCantidadEliminada(cantidadEliminada);
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
