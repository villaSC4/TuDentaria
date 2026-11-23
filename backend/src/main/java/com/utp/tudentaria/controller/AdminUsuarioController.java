package com.utp.tudentaria.controller;

import com.utp.tudentaria.dto.UsuarioAdminForm;
import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("rolesDisponibles", usuarioService.listarRoles());
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new UsuarioAdminForm());
        }
        return "admin/usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") UsuarioAdminForm form,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/usuarios";
        }
        try {
            usuarioService.guardarDesdeAdmin(form);
            redirectAttributes.addFlashAttribute("exito", "Usuario guardado correctamente.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id, authentication.getName());
            redirectAttributes.addFlashAttribute("exito", "Usuario eliminado con éxito.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar este usuario.");
        }
        return "redirect:/admin/usuarios";
    }
}