package com.utp.tudentaria.controller;

import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.EstadoCita;
import com.utp.tudentaria.service.CitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/citas")
public class AdminCitaController {

    private final CitaService citaService;

    public AdminCitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "admin/citas";
    }

    @PostMapping("/actualizar")
    public String actualizarCita(@RequestParam("id") Integer id,
                                 @RequestParam("estado") EstadoCita estado,
                                 @RequestParam(value = "notas", required = false) String notas,
                                 RedirectAttributes redirectAttributes) {
        try {
            citaService.actualizarEstado(id, estado, notas);
            redirectAttributes.addFlashAttribute("exito", "Cita actualizada correctamente.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/citas";
    }

    /** Se borra con POST: un enlace GET podría dispararlo un tercero o un rastreador. */
    @PostMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            citaService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "Cita eliminada con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la cita.");
        }
        return "redirect:/admin/citas";
    }
}