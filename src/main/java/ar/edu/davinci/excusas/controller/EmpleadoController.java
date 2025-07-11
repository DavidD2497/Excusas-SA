package ar.edu.davinci.excusas.controller;

import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public Empleado crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        return empleadoService.crearEmpleado(request.getNombre(), request.getEmail());
    }

    @GetMapping
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoService.obtenerTodosLosEmpleados();
    }

    @GetMapping("/legajo/{legajo}")
    public Empleado obtenerEmpleadoPorLegajo(@PathVariable int legajo) {
        return empleadoService.obtenerEmpleadoPorLegajo(legajo);
    }

    @DeleteMapping("/legajo/{legajo}")
    public String eliminarEmpleado(@PathVariable int legajo) {
        empleadoService.eliminarEmpleado(legajo);
        return "Empleado con legajo " + legajo + " eliminado correctamente";
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public List<Empleado> buscarPorNombre(@PathVariable String nombre) {
        return empleadoService.buscarPorNombre(nombre);
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
