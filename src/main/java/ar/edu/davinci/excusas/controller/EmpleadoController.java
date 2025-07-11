package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.exception.DuplicateEntityException;
import ar.edu.davinci.excusas.exception.EmpleadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private final List<Empleado> empleados = new ArrayList<>();
    private int contadorLegajo = 1000;

    @PostMapping
    public Empleado crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {

        if (existeEmpleadoConEmail(request.getEmail())) {
            throw new DuplicateEntityException("Ya existe un empleado con el email: " + request.getEmail());
        }

        if (existeEmpleadoConNombre(request.getNombre())) {
            throw new DuplicateEntityException("Ya existe un empleado con el nombre: " + request.getNombre());
        }

        Empleado empleado = new Empleado(
                request.getNombre().trim(),
                request.getEmail().toLowerCase().trim(),
                ++contadorLegajo
        );
        empleados.add(empleado);
        return empleado;
    }

    @GetMapping
    public List<Empleado> obtenerTodosLosEmpleados() {
        return new ArrayList<>(empleados);
    }

    @GetMapping("/legajo/{legajo}")
    public Empleado obtenerEmpleadoPorLegajo(@PathVariable int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }

        return empleados.stream()
                .filter(emp -> emp.getLegajo() == legajo)
                .findFirst()
                .orElseThrow(() -> new EmpleadoNotFoundException(legajo));
    }

    @DeleteMapping("/legajo/{legajo}")
    public String eliminarEmpleado(@PathVariable int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }

        boolean eliminado = empleados.removeIf(emp -> emp.getLegajo() == legajo);
        if (eliminado) {
            return "Empleado con legajo " + legajo + " eliminado correctamente";
        } else {
            throw new EmpleadoNotFoundException(legajo);
        }
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public List<Empleado> buscarPorNombre(@PathVariable String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new InvalidDataException("El nombre de búsqueda no puede estar vacío");
        }

        if (nombre.trim().length() < 2) {
            throw new InvalidDataException("El nombre de búsqueda debe tener al menos 2 caracteres");
        }

        List<Empleado> resultado = empleados.stream()
                .filter(emp -> emp.getNombre().toLowerCase().contains(nombre.toLowerCase().trim()))
                .toList();

        if (resultado.isEmpty()) {
            throw new EmpleadoNotFoundException("No se encontraron empleados con el nombre: " + nombre);
        }

        return resultado;
    }

    private boolean existeEmpleadoConEmail(String email) {
        return empleados.stream()
                .anyMatch(emp -> emp.getEmail().equalsIgnoreCase(email.trim()));
    }

    private boolean existeEmpleadoConNombre(String nombre) {
        return empleados.stream()
                .anyMatch(emp -> emp.getNombre().equalsIgnoreCase(nombre.trim()));
    }

    public static class EmpleadoRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        private String nombre;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
        private String email;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
