package com.utp.tudentaria.service;

import com.utp.tudentaria.model.*;
import com.utp.tudentaria.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EspecialidadRepository especialidadRepository;
    private final TratamientoRepository tratamientoRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           RolRepository rolRepository,
                           EspecialidadRepository especialidadRepository,
                           TratamientoRepository tratamientoRepository,
                           DoctorRepository doctorRepository,
                           PacienteRepository pacienteRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.especialidadRepository = especialidadRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Rol adminRol = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("ROLE_ADMIN");
                    return rolRepository.save(nuevoRol);
                });

        rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("ROLE_USER");
                    return rolRepository.save(nuevoRol);
                });

        if (!usuarioRepository.findByEmail("admin@tudentaria.com").isPresent()) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("TuDentaria");
            admin.setEmail("admin@tudentaria.com");
            admin.setPassword(passwordEncoder.encode("123456"));

            java.util.Set<Rol> rolesAdmin = new java.util.HashSet<>();
            rolesAdmin.add(adminRol);
            admin.setRoles(rolesAdmin);

            usuarioRepository.save(admin);
        }

        // Semilla de Especialidades
        Especialidad ortodoncia = especialidadRepository.findByNombre("Ortodoncia y Cirugía")
                .orElseGet(() -> especialidadRepository.save(new Especialidad("Ortodoncia y Cirugía", "Corrección de dientes y mandíbulas alineadas incorrectamente.")));
        
        Especialidad implantologia = especialidadRepository.findByNombre("Implantología y Estética")
                .orElseGet(() -> especialidadRepository.save(new Especialidad("Implantología y Estética", "Reemplazo de piezas dentales perdidas y diseño de sonrisas.")));
        
        especialidadRepository.findByNombre("Endodoncia Avanzada")
                .orElseGet(() -> especialidadRepository.save(new Especialidad("Endodoncia Avanzada", "Tratamiento especializado de conductos radiculares.")));

        // Semilla de Tratamientos
        if (tratamientoRepository.count() == 0) {
            tratamientoRepository.save(new Tratamiento("Limpieza Dental Profunda (Profilaxis)", "Eliminación de sarro, placa bacteriana y pulido dental.", 80.0, 45));
            tratamientoRepository.save(new Tratamiento("Blanqueamiento Dental Láser", "Aclaramiento dental seguro de alta efectividad estética.", 250.0, 60));
            tratamientoRepository.save(new Tratamiento("Ortodoncia con Brackets Metálicos", "Alineación dental integral de arco completo.", 1500.0, 60));
            tratamientoRepository.save(new Tratamiento("Implante Dental de Titanio", "Rehabilitación fija con perno de titanio biocompatible.", 2200.0, 90));
        }

        // Semilla de Doctores de demostración
        if (doctorRepository.count() == 0) {
            Doctor doc1 = new Doctor("Dra. Raquel Villa", "Ortodoncia y Cirugía", "doctor-1.jpg");
            doc1.setEspecialidadObj(ortodoncia);
            doctorRepository.save(doc1);

            Doctor doc2 = new Doctor("Dr. Carlos Mendoza", "Implantología y Estética", "doctor-2.jpg");
            doc2.setEspecialidadObj(implantologia);
            doctorRepository.save(doc2);
        }

        // Semilla de Pacientes de demostración
        if (pacienteRepository.count() == 0) {
            pacienteRepository.save(new Paciente("Juan", "Pérez López", "72345678", "987654321", "juan.perez@example.com"));
            pacienteRepository.save(new Paciente("María", "Gómez Torres", "76543210", "912345678", "maria.gomez@example.com"));
        }
    }
}