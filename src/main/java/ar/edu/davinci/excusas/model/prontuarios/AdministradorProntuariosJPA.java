package ar.edu.davinci.excusas.model.prontuarios;

import ar.edu.davinci.excusas.dto.mapper.EmpleadoMapper;
import ar.edu.davinci.excusas.dto.mapper.ExcusaMapper;
import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.entity.ExcusaEntity;
import ar.edu.davinci.excusas.entity.ProntuarioEntity;
import ar.edu.davinci.excusas.model.empleados.interfaces.IEncargado;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.repository.EmpleadoRepository;
import ar.edu.davinci.excusas.repository.ExcusaRepository;
import ar.edu.davinci.excusas.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdministradorProntuariosJPA extends ObservableBase {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ExcusaRepository excusaRepository;

    @Autowired
    private EmpleadoMapper empleadoMapper;

    @Autowired
    private ExcusaMapper excusaMapper;

    public void notificarExcusaProcesada(Excusa excusa, IEncargado encargadoProcesador) {
        if (this.debeCrearProntuario(excusa, encargadoProcesador)) {
            this.crearYGuardarProntuario(excusa);
        }
    }

    private boolean debeCrearProntuario(Excusa excusa, IEncargado encargadoProcesador) {
        return encargadoProcesador.puedeManejarInverosimil() &&
                excusa.getMotivo().esAceptablePor(encargadoProcesador);
    }

    private void crearYGuardarProntuario(Excusa excusa) {
        // Buscar las entidades en la base de datos
        EmpleadoEntity empleadoEntity = empleadoRepository.findByLegajo(excusa.getLegajoEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Para simplificar, buscaremos la excusa por descripción y empleado
        // En un caso real, deberíamos tener un ID de excusa
        ExcusaEntity excusaEntity = excusaRepository.findByEmpleadoLegajo(excusa.getLegajoEmpleado())
                .stream()
                .filter(e -> e.getDescripcion().equals(excusa.getDescripcion()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Excusa no encontrada"));

        ProntuarioEntity prontuarioEntity = new ProntuarioEntity(
                empleadoEntity,
                excusaEntity,
                excusa.getEmpleado().getLegajo()
        );

        prontuarioRepository.save(prontuarioEntity);
        
        System.out.println("Prontuario creado para empleado: " + excusa.getNombreEmpleado());
        
        // Crear el modelo para notificar a los observadores
        Prontuario prontuario = new Prontuario(
                excusa.getEmpleado(),
                excusa,
                excusa.getEmpleado().getLegajo()
        );
        
        this.notificarObservadores(prontuario);
    }
}
