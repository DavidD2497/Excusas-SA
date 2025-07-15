package ar.edu.davinci.excusas.config;

import ar.edu.davinci.excusas.service.EmpleadoService;
import ar.edu.davinci.excusas.service.ExcusaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        value = "app.data-initializer.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ExcusaService excusaService;

    @Override
    public void run(String... args) throws Exception {

        try {
            crearEmpleadosDePrueba();
            crearExcusasDePrueba();
            procesarExcusasParaProntuarios();

            System.out.println("Datos de prueba creados exitosamente!");
            System.out.println("Puedes ver los datos en: http://localhost:8080/h2-console");
            System.out.println("Endpoints disponibles:");
            System.out.println("   - GET /empleados");
            System.out.println("   - GET /excusas");
            System.out.println("   - GET /prontuarios");

        } catch (Exception e) {
            System.err.println("Error inicializando datos: " + e.getMessage());
        }
    }

    private void crearEmpleadosDePrueba() {

        empleadoService.crearEmpleado("Juan Pérez", "juan.perez@empresa.com");
        empleadoService.crearEmpleado("María García", "maria.garcia@empresa.com");
        empleadoService.crearEmpleado("Carlos López", "carlos.lopez@empresa.com");
        empleadoService.crearEmpleado("Ana Martínez", "ana.martinez@empresa.com");
        empleadoService.crearEmpleado("Pedro Rodríguez", "pedro.rodriguez@empresa.com");
        empleadoService.crearEmpleado("Sofia González", "sofia.gonzalez@empresa.com");
        empleadoService.crearEmpleado("Diego Morales", "diego.morales@empresa.com");
        empleadoService.crearEmpleado("Laura Fernández", "laura.fernandez@empresa.com");

    }

    private void crearExcusasDePrueba() {

        excusaService.crearExcusa(1001, "TRIVIAL", "Se me hizo tarde por el tráfico en la autopista, había un accidente");
        excusaService.crearExcusa(1002, "TRIVIAL", "No sonó mi despertador y me quedé dormida hasta muy tarde");
        excusaService.crearExcusa(1003, "TRIVIAL", "Perdí las llaves de casa y tuve que esperar al cerrajero");

        excusaService.crearExcusa(1004, "PROBLEMA_FAMILIAR", "Tuve que llevar a mi hijo al médico de urgencia por fiebre alta");
        excusaService.crearExcusa(1005, "PROBLEMA_FAMILIAR", "Mi madre se cayó y tuve que acompañarla al hospital");
        excusaService.crearExcusa(1001, "PROBLEMA_FAMILIAR", "Problema familiar urgente que requirió mi atención inmediata");

        excusaService.crearExcusa(1006, "PROBLEMA_ELECTRICO", "Se cortó la luz en todo el barrio y no pude trabajar desde casa");
        excusaService.crearExcusa(1007, "PROBLEMA_ELECTRICO", "Hubo un corte de energía que afectó el transporte público");

        excusaService.crearExcusa(1002, "COMPLEJO", "Tuve un problema legal complejo que requirió atención de abogado urgente");
        excusaService.crearExcusa(1008, "COMPLEJO", "Situación compleja con documentación que requirió múltiples trámites");

        excusaService.crearExcusa(1003, "INVEROSIMIL", "Me secuestraron los extraterrestres y me llevaron a su nave espacial");
        excusaService.crearExcusa(1004, "INVEROSIMIL", "Un dragón bloqueó la entrada de mi casa y no pude salir");
        excusaService.crearExcusa(1005, "INVEROSIMIL", "Viajé accidentalmente en el tiempo y llegué al día equivocado");

    }

    private void procesarExcusasParaProntuarios() {

        try {
            var todasLasExcusas = excusaService.obtenerTodasLasExcusas();

            for (int i = 0; i < todasLasExcusas.size(); i++) {
                var excusa = todasLasExcusas.get(i);

                if (excusa.getMotivo().getClass().getSimpleName().equals("MotivoInverosimil")) {
                    excusaService.procesarExcusa(i);
                }
            }

        } catch (Exception e) {
            System.err.println("⚠Error procesando excusas: " + e.getMessage());
        }
    }
}
