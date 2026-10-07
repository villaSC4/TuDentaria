package com.utp.tudentaria.service;

import com.utp.tudentaria.model.*;
import com.utp.tudentaria.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Roles y catálogo: siempre.
 * Administrador: solo si existen app.admin.email y app.admin.password (ADMIN_EMAIL / ADMIN_PASSWORD).
 * Datos de demostración: solo con app.seed-demo=true (perfil local).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EspecialidadRepository especialidadRepository;
    private final TratamientoRepository tratamientoRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.seed-demo:false}")
    private boolean seedDemo;

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
    public void run(String... args) {
        Rol adminRol = rolRepository.findByNombre(UsuarioService.ROL_ADMIN)
                .orElseGet(() -> rolRepository.save(new Rol(UsuarioService.ROL_ADMIN)));
        rolRepository.findByNombre(UsuarioService.ROL_USER)
                .orElseGet(() -> rolRepository.save(new Rol(UsuarioService.ROL_USER)));

        crearAdministrador(adminRol);

        Especialidad ortodoncia = especialidad("Ortodoncia y Cirugía",
                "Corrección de dientes y mandíbulas alineadas incorrectamente.");
        Especialidad implantologia = especialidad("Implantología y Estética",
                "Reemplazo de piezas dentales perdidas y diseño de sonrisas.");
        especialidad("Endodoncia Avanzada", "Tratamiento especializado de conductos radiculares.");

        if (tratamientoRepository.count() == 0) {
            tratamientoRepository.save(new Tratamiento("Limpieza Dental Profunda (Profilaxis)",
                    "Eliminación de sarro, placa bacteriana y pulido dental.", new BigDecimal("80.00"), 45));
            tratamientoRepository.save(new Tratamiento("Blanqueamiento Dental Láser",
                    "Aclaramiento dental seguro de alta efectividad estética.", new BigDecimal("250.00"), 60));
            tratamientoRepository.save(new Tratamiento("Ortodoncia con Brackets Metálicos",
                    "Alineación dental integral de arco completo.", new BigDecimal("1500.00"), 60));
            tratamientoRepository.save(new Tratamiento("Implante Dental de Titanio",
                    "Rehabilitación fija con perno de titanio biocompatible.", new BigDecimal("2200.00"), 90));
        }

        if (seedDemo) {
            if (doctorRepository.count() == 0) {
                Doctor doc1 = new Doctor("Dra. Raquel Villa", ortodoncia.getNombre(), "doctor-1.jpg");
                doc1.setEspecialidadObj(ortodoncia);
                doctorRepository.save(doc1);

                Doctor doc2 = new Doctor("Dr. Carlos Mendoza", implantologia.getNombre(), "doctor-2.jpg");
                doc2.setEspecialidadObj(implantologia);
                doctorRepository.save(doc2);
            }
            if (pacienteRepository.count() == 0) {
                pacienteRepository.save(new Paciente("Juan", "Pérez López", "72345678", "987654321", "juan.perez@example.com"));
                pacienteRepository.save(new Paciente("María", "Gómez Torres", "76543210", "912345678", "maria.gomez@example.com"));
            }
        }
    }

    private void crearAdministrador(Rol adminRol) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        if (usuarioRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellido("TuDentaria");
        admin.setEmail(adminEmail.trim().toLowerCase());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        Set<Rol> roles = new HashSet<>();
        roles.add(adminRol);
        admin.setRoles(roles);
        usuarioRepository.save(admin);
    }

    private Especialidad especialidad(String nombre, String descripcion) {
        return especialidadRepository.findByNombre(nombre)
                .orElseGet(() -> especialidadRepository.save(new Especialidad(nombre, descripcion)));
    }
}