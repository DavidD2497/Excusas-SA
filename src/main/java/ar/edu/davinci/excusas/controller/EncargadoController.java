package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.service.EncargadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@RestController
@RequestMapping("/encargados")
public class EncargadoController {

    @Autowired
    private EncargadoService encargadoService;

    @GetMapping
    public List<EncargadoService.EncargadoInfo> obtenerTodosLosEncargados() {
        return encargadoService.obtenerTodosLosEncargados();
    }

    @PutMapping("/tipo/{tipo}/modo")
    public ModoResponse cambiarModo(@PathVariable String tipo, @Valid @RequestBody ModoRequest request) {
        String modoAnterior = encargadoService.obtenerEncargado(tipo).getModoActual();
        encargadoService.cambiarModo(tipo, request.getModo());

        ModoResponse response = new ModoResponse();
        response.setMensaje("Modo cambiado a " + request.getModo() + " para " + tipo);
        response.setTipo(tipo);
        response.setModoAnterior(modoAnterior);
        response.setModoNuevo(request.getModo().toUpperCase());
        return response;
    }

    @GetMapping("/tipo/{tipo}")
    public EncargadoService.EncargadoInfo obtenerEncargado(@PathVariable String tipo) {
        return encargadoService.obtenerEncargado(tipo);
    }

    @GetMapping("/buscar/capacidad/{capacidad}")
    public List<EncargadoService.EncargadoInfo> obtenerEncargadosPorCapacidad(@PathVariable String capacidad) {
        return encargadoService.obtenerEncargadosPorCapacidad(capacidad);
    }

    public static class ModoRequest {
        @NotBlank(message = "El modo es obligatorio")
        private String modo;

        public String getModo() { return modo; }
        public void setModo(String modo) { this.modo = modo; }
    }

    public static class ModoResponse {
        private String mensaje;
        private String tipo;
        private String modoAnterior;
        private String modoNuevo;

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getModoAnterior() { return modoAnterior; }
        public void setModoAnterior(String modoAnterior) { this.modoAnterior = modoAnterior; }
        public String getModoNuevo() { return modoNuevo; }
        public void setModoNuevo(String modoNuevo) { this.modoNuevo = modoNuevo; }
    }
}
