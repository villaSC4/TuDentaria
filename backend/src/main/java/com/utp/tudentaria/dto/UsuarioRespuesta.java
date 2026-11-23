package com.utp.tudentaria.dto;
import com.utp.tudentaria.model.Usuario;
import java.util.List;
/** Nunca serializar la entidad Usuario: contiene el hash de contraseña. */
public record UsuarioRespuesta(Integer id, String nombre, String apellido, String email,
                               String dni, String telefono, List<String> roles, Integer idRol) {
    public static UsuarioRespuesta de(Usuario u) {
        var p = u.getPaciente();
        return new UsuarioRespuesta(u.getId(), u.getNombre(), u.getApellido(), u.getEmail(),
                p == null ? "" : p.getDni(), p == null ? "" : p.getTelefono(),
                u.getRoles().stream().map(r -> r.getNombre()).sorted().toList(),
                u.getRoles().stream().map(r -> r.getId()).findFirst().orElse(null));
    }
}
