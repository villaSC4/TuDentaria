package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cita> buscarPorId(Integer id) {
        return citaRepository.findById(id);
    }

    @Override
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }

    @Override
    public void eliminarPorId(Integer id) {
        citaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> listarPorEmail(String email) {
        return citaRepository.findByEmail(email);
    }
}
