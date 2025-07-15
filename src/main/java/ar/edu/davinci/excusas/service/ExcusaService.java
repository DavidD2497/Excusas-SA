package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.dto.mapper.EmpleadoMapper;
import ar.edu.davinci.excusas.dto.mapper.ExcusaMapper;
import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.entity.ExcusaEntity;
import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.exception.ExcusaNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.model.empleados.encargados.CadenaDeEncargados;
import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.excusas.motivos.*;
import ar.edu.davinci.excusas.model.prontuarios.AdministradorProntuariosJPA;
import ar.edu.davinci.excusas.repository.EmpleadoRepository;
import ar.edu.davinci.excusas.repository.ExcusaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ExcusaService {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ExcusaRepository excusaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ExcusaMapper excusaMapper;

    @Autowired
    private EmpleadoMapper empleadoMapper;

    @Autowired
    private AdministradorProntuariosJPA administradorProntuarios;

    private final List<String> tiposMotivosValidos = Arrays.asList(
            "TRIVIAL", "PROBLEMA_ELECTRICO", "PROBLEMA_FAMILIAR", "COMPLEJO", "INVEROSIMIL"
    );

    public Excusa crearExcusa(int legajoEmpleado, String tipoMotivo, String descripcion) {
        validarDatosExcusa(legajoEmpleado, tipoMotivo, descripcion);

        Empleado empleado = empleadoService.obtenerEmpleadoPorLegajo(legajoEmpleado);
        EmpleadoEntity empleadoEntity = empleadoRepository.findByLegajo(legajoEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        validarLimiteExcusasEmpleado(legajoEmpleado);

        ExcusaEntity excusaEntity = excusaMapper.toEntity(empleado, tipoMotivo, descripcion.trim(), empleadoEntity);
        ExcusaEntity savedEntity = excusaRepository.save(excusaEntity);

        return excusaMapper.toModel(savedEntity);
    }

    public void procesarExcusa(int index) {
        List<ExcusaEntity> excusasEntities = excusaRepository.findAll();

        if (index < 0) {
            throw new InvalidDataException("El índice no puede ser negativo");
        }
        if (index >= excusasEntities.size()) {
            throw new ExcusaNotFoundException("Excusa no encontrada en el índice: " + index);
        }

        ExcusaEntity excusaEntity = excusasEntities.get(index);
        Excusa excusa = excusaMapper.toModel(excusaEntity);

        try {
            // Create chain with JPA administrator for proper prontuario persistence
            CadenaDeEncargados cadenaConJPA = new CadenaDeEncargados(administradorProntuarios);
            cadenaConJPA.procesarExcusa(excusa);

            excusaEntity.setProcesada(true);
            excusaRepository.save(excusaEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error en el procesamiento de la excusa: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Excusa> obtenerTodasLasExcusas() {
        return excusaRepository.findAll().stream()
                .map(excusaMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Excusa> obtenerExcusasPorEmpleado(int legajo) {
        validarLegajo(legajo);

        // Verificar que el empleado existe
        empleadoService.obtenerEmpleadoPorLegajo(legajo);

        List<ExcusaEntity> entities = excusaRepository.findByEmpleadoLegajo(legajo);

        if (entities.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas para el empleado con legajo: " + legajo);
        }

        return entities.stream()
                .map(excusaMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Excusa> obtenerExcusasPorTipoMotivo(String tipoMotivo) {
        validarTipoMotivo(tipoMotivo);

        List<ExcusaEntity> entities = excusaRepository.findByTipoMotivoContainingIgnoreCase(tipoMotivo);

        if (entities.isEmpty()) {
            throw new ExcusaNotFoundException("No se encontraron excusas con el tipo de motivo: " + tipoMotivo);
        }

        return entities.stream()
                .map(excusaMapper::toModel)
                .toList();
    }

    private void validarDatosExcusa(int legajoEmpleado, String tipoMotivo, String descripcion) {
        validarLegajo(legajoEmpleado);
        validarTipoMotivo(tipoMotivo);
        validarDescripcion(descripcion);
    }

    private void validarLegajo(int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }
    }

    private void validarTipoMotivo(String tipoMotivo) {
        if (tipoMotivo == null || tipoMotivo.trim().isEmpty()) {
            throw new InvalidDataException("El tipo de motivo es obligatorio");
        }
        if (!tiposMotivosValidos.contains(tipoMotivo.toUpperCase())) {
            throw new InvalidDataException("Tipo de motivo no válido. Tipos válidos: " + tiposMotivosValidos);
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new InvalidDataException("La descripción es obligatoria");
        }
        if (descripcion.trim().length() < 10 || descripcion.trim().length() > 500) {
            throw new InvalidDataException("La descripción debe tener entre 10 y 500 caracteres");
        }
    }

    private void validarLimiteExcusasEmpleado(int legajoEmpleado) {
        long excusasDelEmpleado = excusaRepository.countByEmpleadoLegajo(legajoEmpleado);

        if (excusasDelEmpleado >= 5) {
            throw new BusinessRuleException("El empleado ya tiene el máximo de 5 excusas registradas");
        }
    }
}
