package com.utp.tudentaria.service;

import com.utp.tudentaria.dto.PerfilDTO;
import com.utp.tudentaria.dto.RegistroDTO;
import com.utp.tudentaria.dto.UsuarioAdminForm;
import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.model.Rol;
import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.repository.RolRepository;
import com.utp.tudentaria.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UsuarioService {

    public static final String ROL_ADMIN = "ROLE_ADMIN";
    public static final String ROL_USER = "ROLE_USER";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PacienteService pacienteService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          PacienteService pacienteService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.pacienteService = pacienteService;
    }

    // ---------- Consultas ----------

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long contar() {
        return usuarioRepository.count();
    }

    // ---------- Registro público ----------

    public Usuario registrar(RegistroDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new NegocioException("email", "El correo ya está registrado.");
        }
        if (usuarioRepository.existsByPacienteDni(dto.getDni())) {
            throw new NegocioException("dni", "Ya existe una cuenta registrada con ese DNI.");
        }

        Paciente paciente = pacienteService.obtenerOCrear(dto.getDni(), dto.getNombre().trim(),
                dto.getApellido().trim(), dto.getTelefono(), email);

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setPaciente(paciente);
        // El rol lo decide SIEMPRE el servidor, nunca el formulario.
        usuario.getRoles().add(rolRepository.findByNombre(ROL_USER)
                .orElseThrow(() -> new IllegalStateException("Falta el rol " + ROL_USER)));
        return usuarioRepository.save(usuario);
    }

    // ---------- Perfil propio ----------

    /** @return true si el correo cambió (la sesión actual queda obsoleta y hay que reingresar). */
    public boolean actualizarPerfil(String emailActual, PerfilDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailActual)
                .orElseThrow(() -> new NegocioException("Usuario no encontrado."));

        String nuevoEmail = dto.getEmail().trim().toLowerCase();
        boolean emailCambio = !usuario.getEmail().equalsIgnoreCase(nuevoEmail);
        if (emailCambio) {
            Optional<Usuario> enUso = usuarioRepository.findByEmailIgnoreCase(nuevoEmail);
            if (enUso.isPresent() && !enUso.get().getId().equals(usuario.getId())) {
                throw new NegocioException("email", "El correo ingresado ya está registrado por otro usuario.");
            }
            usuario.setEmail(nuevoEmail);
        }
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());

        Paciente paciente = usuario.getPaciente();
        if (paciente != null) {
            paciente.setNombre(usuario.getNombre());
            paciente.setApellido(usuario.getApellido());
            paciente.setEmail(usuario.getEmail());
            if (dto.getTelefono() != null && !dto.getTelefono().isBlank()) {
                paciente.setTelefono(dto.getTelefono());
            }
        }
        usuarioRepository.save(usuario);
        return emailCambio;
    }

    // ---------- Administración de cuentas ----------

    public void guardarDesdeAdmin(UsuarioAdminForm form) {
        Rol rol = rolRepository.findById(form.getIdRol())
                .orElseThrow(() -> new NegocioException("idRol", "El rol seleccionado no existe."));
        String email = form.getEmail().trim().toLowerCase();
        boolean hayPassword = form.getPassword() != null && !form.getPassword().isBlank();

        if (hayPassword && form.getPassword().length() < 6) {
            throw new NegocioException("password", "La contraseña debe tener al menos 6 caracteres.");
        }

        Usuario usuario;
        if (form.getId() == null) {
            if (!hayPassword) {
                throw new NegocioException("password", "La contraseña es obligatoria para una cuenta nueva.");
            }
            if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
                throw new NegocioException("email", "El correo ya está registrado.");
            }
            usuario = new Usuario();
        } else {
            usuario = usuarioRepository.findById(form.getId())
                    .orElseThrow(() -> new NegocioException("La cuenta que intentas editar no existe."));
            Optional<Usuario> enUso = usuarioRepository.findByEmailIgnoreCase(email);
            if (enUso.isPresent() && !enUso.get().getId().equals(usuario.getId())) {
                throw new NegocioException("email", "El correo ya está registrado por otra cuenta.");
            }
            // No permitir quitar el rol ADMIN al último administrador.
            boolean eraAdmin = tieneRol(usuario, ROL_ADMIN);
            if (eraAdmin && !ROL_ADMIN.equals(rol.getNombre())
                    && usuarioRepository.countByRoles_Nombre(ROL_ADMIN) <= 1) {
                throw new NegocioException("idRol", "No puedes quitar el rol al único administrador del sistema.");
            }
        }

        usuario.setNombre(form.getNombre().trim());
        usuario.setApellido(form.getApellido().trim());
        usuario.setEmail(email);
        if (hayPassword) {
            usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
    }

    public void eliminar(Integer id, String emailActual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La cuenta no existe."));
        if (usuario.getEmail().equalsIgnoreCase(emailActual)) {
            throw new NegocioException("No puedes eliminar tu propia cuenta.");
        }
        if (tieneRol(usuario, ROL_ADMIN) && usuarioRepository.countByRoles_Nombre(ROL_ADMIN) <= 1) {
            throw new NegocioException("No puedes eliminar al único administrador del sistema.");
        }
        usuarioRepository.delete(usuario);
    }

    private boolean tieneRol(Usuario usuario, String nombreRol) {
        return usuario.getRoles().stream().anyMatch(r -> nombreRol.equals(r.getNombre()));
    }
}