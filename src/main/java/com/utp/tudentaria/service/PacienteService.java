package com.utp.tudentaria.service;



import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PacienteService {
  private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * El DNI identifica al paciente: si ya existe se reutiliza SIN modificar sus datos
     * (así nadie altera los datos de otra persona escribiendo su DNI); si no, se crea.
     */
    public Paciente obtenerOCrear(String dni, String nombre, String apellido, String telefono, String email) {
        return pacienteRepository.findByDni(dni)
                .orElseGet(() -> pacienteRepository.save(new Paciente(nombre, apellido, dni, telefono, email)));
    }
}