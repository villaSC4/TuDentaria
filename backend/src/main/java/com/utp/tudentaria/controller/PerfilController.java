package com.utp.tudentaria.controller;

import com.utp.tudentaria.dto.PerfilDTO;
import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String verPerfil(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        if (!model.containsAttribute("perfil")) {
            PerfilDTO dto = new PerfilDTO();
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setEmail(usuario.getEmail());
            if (usuario.getPaciente() != null) {
                dto.setTelefono(usuario.getPaciente().getTelefono());
            }
            model.addAttribute("perfil", dto);
        }
        return "pages/perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarDatos(@Valid @ModelAttribute("perfil") PerfilDTO perfil,
                                  BindingResult result,
                                  Authentication authentication,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);

        if (!result.hasErrors()) {
            try {
                boolean emailCambio = usuarioService.actualizarPerfil(authentication.getName(), perfil);
                if (emailCambio) {
                    // La sesión guarda el correo viejo: se cierra y se pide ingresar con el nuevo.
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                    return "redirect:/login?emailCambiado";
                }
                redirectAttributes.addFlashAttribute("exito", "Tus datos personales se actualizaron correctamente.");
                return "redirect:/perfil";
            } catch (NegocioException e) {
                if (e.getCampo() != null) {
                    result.rejectValue(e.getCampo(), "negocio", e.getMessage());
                } else {
                    result.reject("negocio", e.getMessage());
                }
            }
        }
        return "pages/perfil";
    }
}