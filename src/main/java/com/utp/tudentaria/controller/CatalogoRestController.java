package com.utp.tudentaria.controller;
import com.utp.tudentaria.model.Especialidad;
import com.utp.tudentaria.service.EspecialidadService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
public class CatalogoRestController {
    private final EspecialidadService especialidades;
    public CatalogoRestController(EspecialidadService especialidades) { this.especialidades = especialidades; }
    @GetMapping("/api/especialidades")
    public List<Especialidad> listar() { return especialidades.listarTodas(); }
}
