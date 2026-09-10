package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.service.DoctorService;
import com.utp.tudentaria.service.UploadFileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/admin/doctores")
public class AdminDoctorController {

    private final DoctorService doctorService;
    private final UploadFileService uploadFileService;

    public AdminDoctorController(DoctorService doctorService, UploadFileService uploadFileService) {
        this.doctorService = doctorService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping
    public String listarDoctores(Model model) {
        model.addAttribute("doctores", doctorService.listarTodos());
        if (!model.containsAttribute("doctor")) {
            model.addAttribute("doctor", new Doctor());
        }
        return "admin/doctores";
    }

    @PostMapping("/guardar")
    public String guardarDoctor(@ModelAttribute("doctor") Doctor doctor,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            if (doctor.getId() == null) {
                if (!file.isEmpty()) {
                    String nombreImagen = uploadFileService.guardarImagen(file);
                    doctor.setImagen(nombreImagen);
                }
            } else {
                Doctor existente = doctorService.buscarPorId(doctor.getId()).orElse(null);
                if (existente != null) {
                    if (!file.isEmpty()) {
                        uploadFileService.eliminarImagen(existente.getImagen());
                        String nombreImagen = uploadFileService.guardarImagen(file);
                        doctor.setImagen(nombreImagen);
                    } else {
                        doctor.setImagen(existente.getImagen());
                    }
                }
            }
            doctorService.guardar(doctor);
            redirectAttributes.addFlashAttribute("exito", "Doctor guardado correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen.");
        }
        return "redirect:/admin/doctores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDoctor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.buscarPorId(id).orElse(null);
        if (doctor != null) {
            uploadFileService.eliminarImagen(doctor.getImagen());
            doctorService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "Doctor eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró el doctor.");
        }
        return "redirect:/admin/doctores";
    }
}