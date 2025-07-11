package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.exception.EncargadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.encargados.*;
import ar.edu.davinci.excusas.model.empleados.encargados.modos.*;
import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/encargados")
public class EncargadoController {

    private final Map<String, IManejadorExcusas> encargados = new HashMap<>();
    private final List<String> tiposEncargadosValidos = Arrays.asList(
            "recepcionista", "supervisor", "gerente", "ceo"
    );
    private final List<String> modosValidos = Arrays.asList(
            "NORMAL", "PRODUCTIVO", "VAGO"
    );
    private final List<String> capacidadesValidas = Arrays.asList(
            "TRIVIAL", "MODERADO", "COMPLEJO", "INVEROSIMIL"
    );

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

    @PutMapping("/tipo/{tipo}/modo")
    public ModoResponse cambiarModo(@PathVariable String tipo, @Valid @RequestBody ModoRequest request) {

        if (!tiposEncargadosValidos.contains(tipo.toLowerCase())) {
            throw new InvalidDataException("Tipo de encargado no válido. Tipos válidos: " + tiposEncargadosValidos);
        }

        if (!modosValidos.contains(request.getModo().toUpperCase())) {
            throw new InvalidDataException("Modo no válido. Modos válidos: " + modosValidos);
        }

        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new EncargadoNotFoundException("Encargado no encontrado: " + tipo);
        }

        String modoAnterior = encargado.getModo().getClass().getSimpleName();
        String modoSolicitado = request.getModo().toUpperCase();

        if (modoSolicitado.equals("NORMAL")) {
            encargado.setModo(new ModoNormal());
        } else if (modoSolicitado.equals("PRODUCTIVO")) {
            encargado.setModo(new ModoProductivo());
        } else if (modoSolicitado.equals("VAGO")) {
            encargado.setModo(new ModoVago());
        } else {
            throw new InvalidDataException("Modo no válido: " + request.getModo());
        }

        ModoResponse response = new ModoResponse();
        response.setMensaje("Modo cambiado a " + request.getModo() + " para " + tipo);
        response.setTipo(tipo);
        response.setModoAnterior(modoAnterior);
        response.setModoNuevo(request.getModo().toUpperCase());
        return response;
    }

    @GetMapping("/tipo/{tipo}")
    public EncargadoInfo obtenerEncargado(@PathVariable String tipo) {

        if (!tiposEncargadosValidos.contains(tipo.toLowerCase())) {
            throw new InvalidDataException("Tipo de encargado no válido. Tipos válidos: " + tiposEncargadosValidos);
        }

        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new EncargadoNotFoundException("Encargado no encontrado: " + tipo);
        }

        EncargadoInfo info = new EncargadoInfo();
        info.setTipo(tipo);
        info.setEmailOrigen(encargado.getEmailOrigen());
        info.setModoActual(encargado.getModo().getClass().getSimpleName());
        info.setCapacidades(obtenerCapacidades(encargado));
        return info;
    }

    @GetMapping("/buscar/capacidad/{capacidad}")
    public List<EncargadoInfo> obtenerEncargadosPorCapacidad(@PathVariable String capacidad) {

        if (!capacidadesValidas.contains(capacidad.toUpperCase())) {
            throw new InvalidDataException("Capacidad no válida. Capacidades válidas: " + capacidadesValidas);
        }

        List<EncargadoInfo> resultado = new ArrayList<>();

        for (Map.Entry<String, IManejadorExcusas> entry : encargados.entrySet()) {
            IManejadorExcusas encargado = entry.getValue();
            List<String> capacidades = obtenerCapacidades(encargado);

            if (capacidades.contains(capacidad.toUpperCase())) {
                EncargadoInfo info = new EncargadoInfo();
                info.setTipo(entry.getKey());
                info.setEmailOrigen(encargado.getEmailOrigen());
                info.setModoActual(encargado.getModo().getClass().getSimpleName());
                info.setCapacidades(capacidades);
                resultado.add(info);
            }
        }

        if (resultado.isEmpty()) {
            throw new EncargadoNotFoundException("No se encontraron encargados con la capacidad: " + capacidad);
        }

        return resultado;
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
        @NotBlank(message = "El modo es obligatorio")
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
