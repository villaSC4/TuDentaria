package com.utp.tudentaria.controller;
import com.utp.tudentaria.dto.*;
import com.utp.tudentaria.model.*;
import com.utp.tudentaria.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UsuarioService usuarios;
    private final CitaService citas;
    private final DoctorService doctores;
    private final UploadFileService uploads;
    @org.springframework.beans.factory.annotation.Autowired
    public AdminRestController(UsuarioService usuarios, CitaService citas, DoctorService doctores, UploadFileService uploads) {
        this.usuarios = usuarios; this.citas = citas; this.doctores = doctores; this.uploads = uploads;
    }
    @PostMapping("/doctores/{id}/foto")
    public Doctor foto(@PathVariable Integer id, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        Doctor doctor = doctores.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor no encontrado."));
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) throw new com.utp.tudentaria.exception.NegocioException("Selecciona una imagen de hasta 5 MB.");
        String anterior = doctor.getImagen();
        String nueva = uploads.guardarImagen(file);
        doctor.setImagen(nueva);
        try { doctores.guardar(doctor); }
        catch (RuntimeException ex) { uploads.eliminarImagen(nueva); throw ex; }
        uploads.eliminarImagen(anterior);
        return doctor;
    }
    @GetMapping("/resumen")
    public Map<String,Long> resumen() {
        return Map.of("usuarios", usuarios.contar(), "doctores", (long)doctores.listarTodos().size(),
                "citas", (long)citas.listarTodas().size(), "pendientes", citas.contarPendientes());
    }
    @GetMapping("/usuarios")
    public List<UsuarioRespuesta> usuarios() { return usuarios.listar().stream().map(UsuarioRespuesta::de).toList(); }
    @GetMapping("/roles")
    public List<Rol> roles() { return usuarios.listarRoles(); }
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> crear(@Valid @RequestBody UsuarioAdminForm dto) {
        dto.setId(null); usuarios.guardarDesdeAdmin(dto); return Map.of("mensaje", "Usuario creado.");
    }
    @PutMapping("/usuarios/{id}")
    public Map<String,String> editar(@PathVariable Integer id, @Valid @RequestBody UsuarioAdminForm dto) {
        dto.setId(id); usuarios.guardarDesdeAdmin(dto); return Map.of("mensaje", "Usuario actualizado.");
    }
    @DeleteMapping("/usuarios/{id}")
    public Map<String,String> eliminarUsuario(@PathVariable Integer id, Authentication auth) {
        usuarios.eliminar(id, auth.getName()); return Map.of("mensaje", "Usuario eliminado.");
    }
    @GetMapping("/citas")
    public List<CitaRespuesta> citas() { return citas.listarTodas().stream().map(CitaRespuesta::de).toList(); }
    public record Estado(@NotNull EstadoCita estado, @Size(max=1000) String notas) {}
    @PatchMapping("/citas/{id}")
    public CitaRespuesta estado(@PathVariable Integer id, @Valid @RequestBody Estado dto) {
        if (citas.buscarPorId(id).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada.");
        return CitaRespuesta.de(citas.actualizarEstado(id, dto.estado(), dto.notas()));
    }
    @DeleteMapping("/citas/{id}")
    public Map<String,String> eliminarCita(@PathVariable Integer id) {
        if (citas.buscarPorId(id).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada.");
        citas.eliminarPorId(id); return Map.of("mensaje", "Cita eliminada.");
    }
}
