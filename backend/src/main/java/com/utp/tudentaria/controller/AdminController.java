package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.service.CitaService;
import com.utp.tudentaria.service.DoctorService;
import com.utp.tudentaria.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final DoctorService doctorService;

    public AdminController(UsuarioService usuarioService, CitaService citaService, DoctorService doctorService) {
        this.usuarioService = usuarioService;
        this.citaService = citaService;
        this.doctorService = doctorService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String nombreAdmin = usuarioService.buscarPorEmail(authentication.getName())
                .map(Usuario::getNombre)
                .orElse("Administrador");

        model.addAttribute("nombreAdmin", nombreAdmin);
        model.addAttribute("totalUsuarios", usuarioService.contar());
        model.addAttribute("totalDoctores", doctorService.contar());
        model.addAttribute("citasPendientes", citaService.contarPendientes());
        return "admin/dashboard";
    }
}