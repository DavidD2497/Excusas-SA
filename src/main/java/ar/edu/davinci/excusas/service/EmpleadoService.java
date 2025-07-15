package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.dto.mapper.EmpleadoMapper;
import ar.edu.davinci.excusas.entity.EmpleadoEntity;
import ar.edu.davinci.excusas.exception.DuplicateEntityException;
import ar.edu.davinci.excusas.exception.EmpleadoNotFoundException;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.model.empleados.Empleado;
import ar.edu.davinci.excusas.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmpleadoMapper empleadoMapper;

    private int contadorLegajo = 1000;

    public Empleado crearEmpleado(String nombre, String email) {
        validarDatosEmpleado(nombre, email);
        
        if (empleadoRepository.existsByEmail(email.toLowerCase().trim())) {
            throw new DuplicateEntityException("Ya existe un empleado con el email: " + email);
        }

        if (empleadoRepository.existsByNombre(nombre.trim())) {
            throw new DuplicateEntityException("Ya existe un empleado con el nombre: " + nombre);
        }

        EmpleadoEntity entity = empleadoMapper.toEntity(
                nombre.trim(),
                email.toLowerCase().trim(),
                ++contadorLegajo
        );
        
        EmpleadoEntity savedEntity = empleadoRepository.save(entity);
        return empleadoMapper.toModel(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(empleadoMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public Empleado obtenerEmpleadoPorLegajo(int legajo) {
        validarLegajo(legajo);

        EmpleadoEntity entity = empleadoRepository.findByLegajo(legajo)
                .orElseThrow(() -> new EmpleadoNotFoundException(legajo));
        
        return empleadoMapper.toModel(entity);
    }

    public void eliminarEmpleado(int legajo) {
        validarLegajo(legajo);

        EmpleadoEntity entity = empleadoRepository.findByLegajo(legajo)
                .orElseThrow(() -> new EmpleadoNotFoundException(legajo));
        
        empleadoRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarPorNombre(String nombre) {
        validarNombreBusqueda(nombre);

        List<EmpleadoEntity> entities = empleadoRepository.findByNombreContainingIgnoreCase(nombre.trim());

        if (entities.isEmpty()) {
            throw new EmpleadoNotFoundException("No se encontraron empleados con el nombre: " + nombre);
        }

        return entities.stream()
                .map(empleadoMapper::toModel)
                .toList();
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
}
