package ar.edu.davinci.excusas.service;

import ar.edu.davinci.excusas.dto.mapper.ProntuarioMapper;
import ar.edu.davinci.excusas.entity.ProntuarioEntity;
import ar.edu.davinci.excusas.exception.InvalidDataException;
import ar.edu.davinci.excusas.exception.BusinessRuleException;
import ar.edu.davinci.excusas.model.prontuarios.Prontuario;
import ar.edu.davinci.excusas.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private ProntuarioMapper prontuarioMapper;

    @Transactional(readOnly = true)
    public List<Prontuario> obtenerTodosLosProntuarios() {
        return prontuarioRepository.findAll().stream()
                .map(prontuarioMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Prontuario> obtenerProntuariosPorEmpleado(int legajo) {
        validarLegajo(legajo);

        List<ProntuarioEntity> entities = prontuarioRepository.findByLegajo(legajo);

        if (entities.isEmpty()) {
            throw new BusinessRuleException("No se encontraron prontuarios para el empleado con legajo: " + legajo);
        }

        return entities.stream()
                .map(prontuarioMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public int contarProntuarios() {
        return (int) prontuarioRepository.count();
    }

    public int limpiarProntuarios() {
        long cantidadAnterior = prontuarioRepository.count();

        if (cantidadAnterior == 0) {
            throw new BusinessRuleException("No hay prontuarios para eliminar");
        }

        prontuarioRepository.deleteAll();
        return (int) cantidadAnterior;
    }

    private void validarLegajo(int legajo) {
        if (legajo <= 1000) {
            throw new InvalidDataException("El legajo debe ser mayor a 1000");
        }
    }
}
