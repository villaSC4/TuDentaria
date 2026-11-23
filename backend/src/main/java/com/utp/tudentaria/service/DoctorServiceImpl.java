package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> listarTodos() {
        return doctorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Doctor> buscarPorId(Integer id) {
        return doctorRepository.findById(id);
    }

    @Override
    public Doctor guardar(Doctor doctor) {
        // La relación con Especialidad manda: el texto se deriva de ella para que nunca se contradigan.
        if (doctor.getEspecialidadObj() != null && doctor.getEspecialidadObj().getNombre() != null) {
            doctor.setEspecialidad(doctor.getEspecialidadObj().getNombre());
        }
        return doctorRepository.save(doctor);
    }

    @Override
    public void eliminarPorId(Integer id) {
        doctorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Integer id) {
        return doctorRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return doctorRepository.count();
    }
}
