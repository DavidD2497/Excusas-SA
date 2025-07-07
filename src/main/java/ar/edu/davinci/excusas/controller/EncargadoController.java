package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.empleados.encargados.*;
import ar.edu.davinci.excusas.model.empleados.encargados.modos.*;
import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/encargados")
public class EncargadoController {

    private final Map<String, IManejadorExcusas> encargados = new HashMap<>();

    public EncargadoController() {
        inicializarEncargados();
    }

    private void inicializarEncargados() {
        encargados.put("recepcionista", new Recepcionista("Laura Recep", "laura@excusas.com", 2001));
        encargados.put("supervisor", new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002));
        encargados.put("gerente", new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003));
        encargados.put("ceo", new CEO("Roberto CEO", "roberto@excusas.com", 2004));
    }

    @GetMapping
    public List<EncargadoInfo> obtenerTodosLosEncargados() {
        List<EncargadoInfo> info = new ArrayList<>();
        for (Map.Entry<String, IManejadorExcusas> entry : encargados.entrySet()) {
            IManejadorExcusas encargado = entry.getValue();
            EncargadoInfo encargadoInfo = new EncargadoInfo();
            encargadoInfo.setTipo(entry.getKey());
            encargadoInfo.setEmailOrigen(encargado.getEmailOrigen());
            encargadoInfo.setModoActual(encargado.getModo().getClass().getSimpleName());
            encargadoInfo.setCapacidades(obtenerCapacidades(encargado));
            info.add(encargadoInfo);
        }
        return info;
    }

    @PutMapping("/{tipo}/modo")
    public String cambiarModo(@PathVariable String tipo, @RequestBody ModoRequest request) {
        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new RuntimeException("Encargado no encontrado: " + tipo);
        }

        switch (request.getModo().toUpperCase()) {
            case "NORMAL" -> encargado.setModo(new ModoNormal());
            case "PRODUCTIVO" -> encargado.setModo(new ModoProductivo());
            case "VAGO" -> encargado.setModo(new ModoVago());
            default -> throw new RuntimeException("Modo no válido: " + request.getModo());
        }

        return "Modo cambiado a " + request.getModo() + " para " + tipo;
    }

    @GetMapping("/{tipo}")
    public EncargadoInfo obtenerEncargado(@PathVariable String tipo) {
        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new RuntimeException("Encargado no encontrado: " + tipo);
        }

        EncargadoInfo info = new EncargadoInfo();
        info.setTipo(tipo);
        info.setEmailOrigen(encargado.getEmailOrigen());
        info.setModoActual(encargado.getModo().getClass().getSimpleName());
        info.setCapacidades(obtenerCapacidades(encargado));
        return info;
    }

    private List<String> obtenerCapacidades(IManejadorExcusas encargado) {
        List<String> capacidades = new ArrayList<>();
        if (encargado.puedeManejarTrivial()) capacidades.add("TRIVIAL");
        if (encargado.puedeManejarModerado()) capacidades.add("MODERADO");
        if (encargado.puedeManejarComplejo()) capacidades.add("COMPLEJO");
        if (encargado.puedeManejarInverosimil()) capacidades.add("INVEROSIMIL");
        return capacidades;
    }

    public static class ModoRequest {
        private String modo;

        public String getModo() { return modo; }
        public void setModo(String modo) { this.modo = modo; }
    }

    public static class EncargadoInfo {
        private String tipo;
        private String emailOrigen;
        private String modoActual;
        private List<String> capacidades;

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getEmailOrigen() { return emailOrigen; }
        public void setEmailOrigen(String emailOrigen) { this.emailOrigen = emailOrigen; }
        public String getModoActual() { return modoActual; }
        public void setModoActual(String modoActual) { this.modoActual = modoActual; }
        public List<String> getCapacidades() { return capacidades; }
        public void setCapacidades(List<String> capacidades) { this.capacidades = capacidades; }
    }
}
