package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.interfaces.IEmpleado;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final List<Empleado> empleados = new ArrayList<>();
    private int contadorLegajo = 1000;

    @PostMapping
    public Empleado crearEmpleado(@RequestBody EmpleadoRequest request) {
        Empleado empleado = new Empleado(
                request.getNombre(),
                request.getEmail(),
                ++contadorLegajo
        );
        empleados.add(empleado);
        return empleado;
    }

    @GetMapping
    public List<Empleado> obtenerTodosLosEmpleados() {
        return new ArrayList<>(empleados);
    }

    @GetMapping("/{legajo}")
    public Empleado obtenerEmpleadoPorLegajo(@PathVariable int legajo) {
        return empleados.stream()
                .filter(emp -> emp.getLegajo() == legajo)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @DeleteMapping("/{legajo}")
    public void eliminarEmpleado(@PathVariable int legajo) {
        empleados.removeIf(emp -> emp.getLegajo() == legajo);
    }

    @GetMapping("/buscar")
    public List<Empleado> buscarPorNombre(@RequestParam String nombre) {
        return empleados.stream()
                .filter(emp -> emp.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    public static class EmpleadoRequest {
        private String nombre;
        private String email;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
