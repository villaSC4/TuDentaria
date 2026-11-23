package com.utp.tudentaria.service;

import com.utp.tudentaria.dto.SolicitudCitaDTO;
import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.model.EstadoCita;
import com.utp.tudentaria.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listarTodas();
    Optional<Cita> buscarPorId(Integer id);
    Cita guardar(Cita cita);
    void eliminarPorId(Integer id);
    List<Cita> listarPorEmail(String email);

    /** Registra una solicitud validando horario, disponibilidad y vinculándola al paciente (DNI). */
    Cita solicitar(SolicitudCitaDTO dto, Paciente pacienteLogueado);

    /** Cambia estado y notas de una cita existente. */
    Cita actualizarEstado(Integer id, EstadoCita estado, String notas);

    long contarPendientes();
}