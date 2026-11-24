package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Tratamiento;
import com.utp.tudentaria.repository.TratamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TratamientoServiceImpl implements TratamientoService {

    private final TratamientoRepository tratamientoRepository;

    public TratamientoServiceImpl(TratamientoRepository tratamientoRepository) {
        this.tratamientoRepository = tratamientoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tratamiento> listarTodos() {
        return tratamientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tratamiento> buscarPorId(Integer id) {
        return tratamientoRepository.findById(id);
    }

    @Override
    public Tratamiento guardar(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }

    @Override
    public void eliminarPorId(Integer id) {
        tratamientoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Integer id) {
        return tratamientoRepository.existsById(id);
    }
}
