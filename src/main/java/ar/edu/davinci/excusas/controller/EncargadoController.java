package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.service.EncargadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

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

    // Endpoint según consigna exacta
    @PostMapping
    public CrearEncargadoResponse crearEncargado(@Valid @RequestBody CrearEncargadoRequest request) {
        EncargadoService.EncargadoInfo nuevoEncargado = encargadoService.crearEncargadoDinamico(
                request.getTipo(),
                request.getNombre(),
                request.getEmail(),
                request.getCapacidades()
        );

        CrearEncargadoResponse response = new CrearEncargadoResponse();
        response.setMensaje("Encargado creado exitosamente");
        response.setTipo(nuevoEncargado.getTipo());
        response.setNombre(request.getNombre());
        response.setEmail(nuevoEncargado.getEmailOrigen());
        response.setCapacidades(nuevoEncargado.getCapacidades());
        return response;
    }

    // Endpoint según consigna exacta
    @PutMapping("/modo")
    public ModoResponse cambiarModoGeneral(@Valid @RequestBody ModoRequestGeneral request) {
        String modoAnterior = encargadoService.obtenerEncargado(request.getTipo()).getModoActual();
        encargadoService.cambiarModo(request.getTipo(), request.getModo());

        ModoResponse response = new ModoResponse();
        response.setMensaje("Modo cambiado a " + request.getModo() + " para " + request.getTipo());
        response.setTipo(request.getTipo());
        response.setModoAnterior(modoAnterior);
        response.setModoNuevo(request.getModo().toUpperCase());
        return response;
    }

    // Mantener endpoint anterior por compatibilidad
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

    public static class CrearEncargadoRequest {
        @NotBlank(message = "El tipo de encargado es obligatorio")
        private String tipo;

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "El email es obligatorio")
        private String email;

        @NotEmpty(message = "Debe especificar al menos una capacidad")
        private List<String> capacidades;

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public List<String> getCapacidades() { return capacidades; }
        public void setCapacidades(List<String> capacidades) { this.capacidades = capacidades; }
    }

    public static class CrearEncargadoResponse {
        private String mensaje;
        private String tipo;
        private String nombre;
        private String email;
        private List<String> capacidades;

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public List<String> getCapacidades() { return capacidades; }
        public void setCapacidades(List<String> capacidades) { this.capacidades = capacidades; }
    }

    public static class ModoRequestGeneral {
        @NotBlank(message = "El tipo de encargado es obligatorio")
        private String tipo;

        @NotBlank(message = "El modo es obligatorio")
        private String modo;

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getModo() { return modo; }
        public void setModo(String modo) { this.modo = modo; }
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
