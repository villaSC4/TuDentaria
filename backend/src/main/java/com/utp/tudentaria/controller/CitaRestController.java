package com.utp.tudentaria.controller;
import com.utp.tudentaria.dto.*;
import com.utp.tudentaria.service.*;
import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.repository.CitaRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/citas")
public class CitaRestController {
    private final CitaService citas;
    private final UsuarioService usuarios;
    private final CitaRepository repository;
    public CitaRestController(CitaService citas, UsuarioService usuarios, CitaRepository repository) {
        this.citas = citas; this.usuarios = usuarios; this.repository = repository;
    }
    @PostMapping
    public ResponseEntity<CitaRespuesta> solicitar(@Valid @RequestBody SolicitudCitaDTO dto, Authentication auth) {
        Paciente p = auth == null ? null : usuarios.buscarPorEmail(auth.getName()).orElseThrow().getPaciente();
        return ResponseEntity.status(201).body(CitaRespuesta.de(citas.solicitar(dto, p)));
    }
    @GetMapping("/mias")
    public List<CitaRespuesta> mias(Authentication auth) {
        var p = usuarios.buscarPorEmail(auth.getName()).orElseThrow().getPaciente();
        // Identidad estable: un cambio de correo no pierde el historial.
        return p == null ? List.of() : repository.findByPacienteIdOrderByFechaDescHoraDesc(p.getId()).stream().map(CitaRespuesta::de).toList();
    }
}
