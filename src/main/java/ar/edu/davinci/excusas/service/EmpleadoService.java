package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.exception.DuplicateEntityException;
import ar.edu.davinci.excusas.exception.EmpleadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpleadoService {

    private final List<Empleado> empleados = new ArrayList<>();
    private int contadorLegajo = 1000;

    public Empleado crearEmpleado(String nombre, String email) {
        validarDatosEmpleado(nombre, email);
        
        if (existeEmpleadoConEmail(email)) {
            throw new DuplicateEntityException("Ya existe un empleado con el email: " + email);
        }

        if (existeEmpleadoConNombre(nombre)) {
            throw new DuplicateEntityException("Ya existe un empleado con el nombre: " + nombre);
        }

        Empleado empleado = new Empleado(
                nombre.trim(),
                email.toLowerCase().trim(),
                ++contadorLegajo
        );
        empleados.add(empleado);
        return empleado;
    }

    public List<Empleado> obtenerTodosLosEmpleados() {
        return new ArrayList<>(empleados);
    }

    public Empleado obtenerEmpleadoPorLegajo(int legajo) {
        validarLegajo(legajo);

        return empleados.stream()
                .filter(emp -> emp.getLegajo() == legajo)
                .findFirst()
                .orElseThrow(() -> new EmpleadoNotFoundException(legajo));
    }

    public void eliminarEmpleado(int legajo) {
        validarLegajo(legajo);

        boolean eliminado = empleados.removeIf(emp -> emp.getLegajo() == legajo);
        if (!eliminado) {
            throw new EmpleadoNotFoundException(legajo);
        }
    }

    public List<Empleado> buscarPorNombre(String nombre) {
        validarNombreBusqueda(nombre);

        List<Empleado> resultado = empleados.stream()
                .filter(emp -> emp.getNombre().toLowerCase().contains(nombre.toLowerCase().trim()))
                .toList();

        if (resultado.isEmpty()) {
            throw new EmpleadoNotFoundException("No se encontraron empleados con el nombre: " + nombre);
        }

        return resultado;
    }

    private void validarDatosEmpleado(String nombre, String email) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new InvalidDataException("El nombre es obligatorio");
        }
        if (nombre.trim().length() < 2 || nombre.trim().length() > 50) {
            throw new InvalidDataException("El nombre debe tener entre 2 y 50 caracteres");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidDataException("El email es obligatorio");
        }
        if (email.trim().length() > 100) {
            throw new InvalidDataException("El email no puede exceder los 100 caracteres");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidDataException("El formato del email no es válido");
        }
    }

    private void validarLegajo(int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }
    }

    private void validarNombreBusqueda(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new InvalidDataException("El nombre de búsqueda no puede estar vacío");
        }
        if (nombre.trim().length() < 2) {
            throw new InvalidDataException("El nombre de búsqueda debe tener al menos 2 caracteres");
        }
    }

    private boolean existeEmpleadoConEmail(String email) {
        return empleados.stream()
                .anyMatch(emp -> emp.getEmail().equalsIgnoreCase(email.trim()));
    }

    private boolean existeEmpleadoConNombre(String nombre) {
        return empleados.stream()
                .anyMatch(emp -> emp.getNombre().equalsIgnoreCase(nombre.trim()));
    }
}
