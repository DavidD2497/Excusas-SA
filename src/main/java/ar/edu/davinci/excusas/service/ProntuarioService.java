package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuarios;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProntuarioService {

    private final AdministradorProntuarios administrador = AdministradorProntuarios.getInstance();

    public List<Prontuario> obtenerTodosLosProntuarios() {
        return administrador.getProntuarios();
    }

    public List<Prontuario> obtenerProntuariosPorEmpleado(int legajo) {
        validarLegajo(legajo);

        List<Prontuario> prontuarios = administrador.getProntuarios().stream()
                .filter(prontuario -> prontuario.getLegajo() == legajo)
                .toList();

        if (prontuarios.isEmpty()) {
            throw new BusinessRuleException("No se encontraron prontuarios para el empleado con legajo: " + legajo);
        }

        return prontuarios;
    }

    public int contarProntuarios() {
        return administrador.getProntuarios().size();
    }

    public int limpiarProntuarios() {
        int cantidadAnterior = administrador.getProntuarios().size();

        if (cantidadAnterior == 0) {
            throw new BusinessRuleException("No hay prontuarios para eliminar");
        }

        administrador.limpiarProntuarios();
        return cantidadAnterior;
    }

    private void validarLegajo(int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }
    }
}
