package com.utp.tudentaria.controller;
import com.utp.tudentaria.dto.*;
import com.utp.tudentaria.service.*;
import com.utp.tudentaria.exception.NegocioException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokens;
    private final UsuarioService usuarios;
    public AuthRestController(AuthenticationManager authenticationManager, TokenService tokens, UsuarioService usuarios) {
        this.authenticationManager = authenticationManager; this.tokens = tokens; this.usuarios = usuarios;
    }
    public record Login(@NotBlank @Email String email, @NotBlank @Size(max=72) String password) {}
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        try {
            var auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                    login.email().trim().toLowerCase(), login.password()));
            var token = tokens.emitir(auth);
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of(
                    "accessToken", token.accessToken(), "tokenType", token.tokenType(), "expiresIn", token.expiresIn(),
                    "usuario", UsuarioRespuesta.de(usuarios.buscarPorEmail(auth.getName()).orElseThrow())));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Correo o contraseña incorrectos."));
        }
    }
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO registro) {
        if (!registro.getPassword().equals(registro.getConfirmarPassword()))
            throw new NegocioException("confirmarPassword", "Las contraseñas no coinciden.");
        return ResponseEntity.status(201).body(UsuarioRespuesta.de(usuarios.registrar(registro)));
    }
}
