package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> listarTodos();
    Optional<Doctor> buscarPorId(Integer id);
    Doctor guardar(Doctor doctor);
    void eliminarPorId(Integer id);
    boolean existePorId(Integer id);
}
