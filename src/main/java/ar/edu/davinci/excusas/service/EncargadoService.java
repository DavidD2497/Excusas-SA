package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.EncargadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.encargados.*;
import ar.edu.davinci.excusas.model.empleados.encargados.modos.*;
import ar.edu.davinci.excusas.model.empleados.interfaces.IManejadorExcusas;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EncargadoService {

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

    public EncargadoService() {
        inicializarEncargados();
    }

    public List<EncargadoInfo> obtenerTodosLosEncargados() {
        List<EncargadoInfo> info = new ArrayList<>();
        for (Map.Entry<String, IManejadorExcusas> entry : encargados.entrySet()) {
            IManejadorExcusas encargado = entry.getValue();
            EncargadoInfo encargadoInfo = crearEncargadoInfo(entry.getKey(), encargado);
            info.add(encargadoInfo);
        }
        return info;
    }

    public EncargadoInfo obtenerEncargado(String tipo) {
        validarTipoEncargado(tipo);
        
        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new EncargadoNotFoundException("Encargado no encontrado: " + tipo);
        }

        return crearEncargadoInfo(tipo, encargado);
    }

    public void cambiarModo(String tipo, String modo) {
        validarTipoEncargado(tipo);
        validarModo(modo);

        IManejadorExcusas encargado = encargados.get(tipo.toLowerCase());
        if (encargado == null) {
            throw new EncargadoNotFoundException("Encargado no encontrado: " + tipo);
        }

        asignarModo(encargado, modo.toUpperCase());
    }

    public List<EncargadoInfo> obtenerEncargadosPorCapacidad(String capacidad) {
        validarCapacidad(capacidad);

        List<EncargadoInfo> resultado = new ArrayList<>();

        for (Map.Entry<String, IManejadorExcusas> entry : encargados.entrySet()) {
            IManejadorExcusas encargado = entry.getValue();
            List<String> capacidades = obtenerCapacidades(encargado);

            if (capacidades.contains(capacidad.toUpperCase())) {
                EncargadoInfo info = crearEncargadoInfo(entry.getKey(), encargado);
                resultado.add(info);
            }
        }

        if (resultado.isEmpty()) {
            throw new EncargadoNotFoundException("No se encontraron encargados con la capacidad: " + capacidad);
        }

        return resultado;
    }

    private void inicializarEncargados() {
        encargados.put("recepcionista", new Recepcionista("Laura Recep", "laura@excusas.com", 2001));
        encargados.put("supervisor", new SupervisorArea("Pedro Super", "pedro@excusas.com", 2002));
        encargados.put("gerente", new GerenteRecursosHumanos("Sofia Gerente", "sofia@excusas.com", 2003));
        encargados.put("ceo", new CEO("Roberto CEO", "roberto@excusas.com", 2004));
    }

    private EncargadoInfo crearEncargadoInfo(String tipo, IManejadorExcusas encargado) {
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

    private void asignarModo(IManejadorExcusas encargado, String modo) {
        switch (modo) {
            case "NORMAL":
                encargado.setModo(new ModoNormal());
                break;
            case "PRODUCTIVO":
                encargado.setModo(new ModoProductivo());
                break;
            case "VAGO":
                encargado.setModo(new ModoVago());
                break;
            default:
                throw new InvalidDataException("Modo no válido: " + modo);
        }
    }

    private void validarTipoEncargado(String tipo) {
        if (!tiposEncargadosValidos.contains(tipo.toLowerCase())) {
            throw new InvalidDataException("Tipo de encargado no válido. Tipos válidos: " + tiposEncargadosValidos);
        }
    }

    private void validarModo(String modo) {
        if (!modosValidos.contains(modo.toUpperCase())) {
            throw new InvalidDataException("Modo no válido. Modos válidos: " + modosValidos);
        }
    }

    private void validarCapacidad(String capacidad) {
        if (!capacidadesValidas.contains(capacidad.toUpperCase())) {
            throw new InvalidDataException("Capacidad no válida. Capacidades válidas: " + capacidadesValidas);
        }
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
