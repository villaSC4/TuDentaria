package com.utp.tudentaria.service;

import com.utp.tudentaria.dto.SolicitudCitaDTO;
import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.model.EstadoCita;
import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.model.Tratamiento;
import com.utp.tudentaria.repository.CitaRepository;
import com.utp.tudentaria.repository.DoctorRepository;
import com.utp.tudentaria.repository.TratamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository;
    private final TratamientoRepository tratamientoRepository;
    private final PacienteService pacienteService;

    public CitaServiceImpl(CitaRepository citaRepository,
                           DoctorRepository doctorRepository,
                           TratamientoRepository tratamientoRepository,
                           PacienteService pacienteService) {
        this.citaRepository = citaRepository;
        this.doctorRepository = doctorRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.pacienteService = pacienteService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> listarTodas() {
        return citaRepository.findAllByOrderByFechaDescHoraDesc();
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

    @Override
    public Cita solicitar(SolicitudCitaDTO dto, Paciente pacienteLogueado) {
        validarHorario(dto.getFecha(), dto.getHora());

        Doctor doctor = null;
        if (dto.getDoctorId() != null) {
            doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new NegocioException("doctorId", "El doctor seleccionado no existe."));
            if (citaRepository.existsByDoctorIdAndFechaAndHoraAndEstadoNot(
                    doctor.getId(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA)) {
                throw new NegocioException("hora", "Ese doctor ya tiene una cita en esa fecha y hora. Elige otro horario.");
            }
        }

        Tratamiento tratamiento = null;
        if (dto.getTratamientoId() != null) {
            tratamiento = tratamientoRepository.findById(dto.getTratamientoId())
                    .orElseThrow(() -> new NegocioException("tratamientoId", "El tratamiento seleccionado no existe."));
        }

        Paciente paciente;
        Cita cita = new Cita();
        if (pacienteLogueado != null) {
            // Usuario con sesión: se usa SU identidad, se ignoran los datos enviados.
            paciente = pacienteLogueado;
            cita.setNombre(paciente.getNombre());
            cita.setApellido(paciente.getApellido());
            cita.setEmail(paciente.getEmail());
            cita.setTelephone(paciente.getTelefono());
        } else {
            paciente = pacienteService.obtenerOCrear(dto.getDni(), dto.getNombre(), dto.getApellido(),
                    dto.getTelefono(), dto.getEmail());
            cita.setNombre(dto.getNombre());
            cita.setApellido(dto.getApellido());
            cita.setEmail(dto.getEmail());
            cita.setTelephone(dto.getTelefono());
        }

        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setTratamiento(tratamiento);
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setMotivo(dto.getMotivo());
        cita.setNotas(dto.getNotas());
        cita.setEstado(EstadoCita.PENDIENTE);
        return citaRepository.save(cita);
    }

    @Override
    public Cita actualizarEstado(Integer id, EstadoCita estado, String notas) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("No se encontró la cita especificada."));
        cita.setEstado(estado);
        cita.setNotas(notas);
        return citaRepository.save(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPendientes() {
        return citaRepository.countByEstado(EstadoCita.PENDIENTE);
    }

    /** Horario de la clínica: L-V 8:00-18:00, Sáb 9:00-13:00, Dom cerrado. Citas en punto. */
    static void validarHorario(LocalDate fecha, LocalTime hora) {
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SUNDAY) {
            throw new NegocioException("fecha", "Los domingos la clínica está cerrada.");
        }
        int h = hora.getHour();
        boolean enPunto = hora.getMinute() == 0;
        boolean dentroDeHorario = dia == DayOfWeek.SATURDAY ? (h >= 9 && h <= 12) : (h >= 8 && h <= 17);
        if (!enPunto || !dentroDeHorario) {
            throw new NegocioException("hora", dia == DayOfWeek.SATURDAY
                    ? "Los sábados atendemos de 9:00 a 13:00 (citas en punto)."
                    : "De lunes a viernes atendemos de 8:00 a 18:00 (citas en punto).");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new NegocioException("hora", "La hora elegida ya pasó.");
        }
    }
}