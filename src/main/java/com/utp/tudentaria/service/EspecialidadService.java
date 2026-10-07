package com.utp.tudentaria.service;



import com.utp.tudentaria.model.Especialidad;
import com.utp.tudentaria.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EspecialidadService {
 
    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    public List<Especialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    public Optional<Especialidad> buscarPorId(Integer id) {
        return especialidadRepository.findById(id);
    }
}