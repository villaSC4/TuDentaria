package com.utp.tudentaria.dto;
import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.model.EstadoCita;
import java.time.LocalDate;
import java.time.LocalTime;
public record CitaRespuesta(Integer id, String nombre, String apellido, String email, String telefono,
        LocalDate fecha, LocalTime hora, String motivo, String notas, EstadoCita estado,
        String doctor, String tratamiento) {
    public static CitaRespuesta de(Cita c) {
        return new CitaRespuesta(c.getId(), c.getNombre(), c.getApellido(), c.getEmail(), c.getTelephone(),
            c.getFecha(), c.getHora(), c.getMotivo(), c.getNotas(), c.getEstado(),
            c.getDoctor() == null ? "Por asignar" : c.getDoctor().getNombre(),
            c.getTratamiento() == null ? "Evaluación" : c.getTratamiento().getNombre());
    }
}
