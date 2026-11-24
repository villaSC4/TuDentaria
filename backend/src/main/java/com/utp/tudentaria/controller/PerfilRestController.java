package com.utp.tudentaria.controller;
import com.utp.tudentaria.dto.*;
import com.utp.tudentaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/perfil")
public class PerfilRestController {
    private final UsuarioService usuarios;
    public PerfilRestController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @GetMapping
    public UsuarioRespuesta perfil(Authentication auth) {
        return UsuarioRespuesta.de(usuarios.buscarPorEmail(auth.getName()).orElseThrow());
    }
    @PutMapping
    public Map<String,Object> actualizar(@Valid @RequestBody PerfilDTO dto, Authentication auth) {
        boolean cambio = usuarios.actualizarPerfil(auth.getName(), dto);
        return Map.of("mensaje", cambio ? "Correo actualizado. Ingresa nuevamente." : "Perfil actualizado.", "requiereLogin", cambio);
    }
}
