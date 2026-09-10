package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Cita;
import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listarTodas();
    Optional<Cita> buscarPorId(Integer id);
    Cita guardar(Cita cita);
    void eliminarPorId(Integer id);
    List<Cita> listarPorEmail(String email);
}
