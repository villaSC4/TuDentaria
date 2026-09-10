package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Cita;
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
                                 @RequestParam("estado") String estado,
                                 @RequestParam(value = "notas", required = false) String notas,
                                 RedirectAttributes redirectAttributes) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        if (cita != null) {
            cita.setEstado(estado);
            cita.setNotas(notas);
            citaService.guardar(cita);
            redirectAttributes.addFlashAttribute("exito", "Cita actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cita especificada.");
        }
        return "redirect:/admin/citas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            citaService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "Cita eliminada con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la cita.");
        }
        return "redirect:/admin/citas";
    }
}