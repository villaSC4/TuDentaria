package com.utp.tudentaria.repository;

import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    long countByEstado(EstadoCita estado);

    List<Cita> findByEmail(String email);

    List<Cita> findAllByOrderByFechaDescHoraDesc();

    /** ¿El doctor ya tiene una cita (no cancelada) en ese día y hora? */
    boolean existsByDoctorIdAndFechaAndHoraAndEstadoNot(Integer doctorId, LocalDate fecha, LocalTime hora, EstadoCita estado);
}