package com.utp.tudentaria.controller;

import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.model.Especialidad;
import com.utp.tudentaria.service.DoctorService;
import com.utp.tudentaria.service.EspecialidadService;
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
    private final EspecialidadService especialidadService;
    private final UploadFileService uploadFileService;

    public AdminDoctorController(DoctorService doctorService,
                                 EspecialidadService especialidadService,
                                 UploadFileService uploadFileService) {
        this.doctorService = doctorService;
        this.especialidadService = especialidadService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping
    public String listarDoctores(Model model) {
        model.addAttribute("doctores", doctorService.listarTodos());
        model.addAttribute("especialidades", especialidadService.listarTodas());
        if (!model.containsAttribute("doctor")) {
            model.addAttribute("doctor", new Doctor());
        }
        return "admin/doctores";
    }

    @PostMapping("/guardar")
    public String guardarDoctor(@ModelAttribute("doctor") Doctor formulario,
                                @RequestParam("especialidadId") Integer especialidadId,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            if (formulario.getNombre() == null || formulario.getNombre().isBlank()) {
                throw new NegocioException("El nombre del doctor es obligatorio.");
            }
            Especialidad especialidad = especialidadService.buscarPorId(especialidadId)
                    .orElseThrow(() -> new NegocioException("La especialidad seleccionada no existe."));

            Doctor doctor;
            String imagenAnterior = null;
            if (formulario.getId() == null) {
                if (file.isEmpty()) {
                    throw new NegocioException("La fotografía es obligatoria para un doctor nuevo.");
                }
                doctor = new Doctor();
            } else {
                doctor = doctorService.buscarPorId(formulario.getId())
                        .orElseThrow(() -> new NegocioException("No se encontró el doctor."));
                imagenAnterior = doctor.getImagen();
            }

            // Se copian solo los campos permitidos (no se acepta el objeto completo del formulario).
            doctor.setNombre(formulario.getNombre().trim());
            doctor.setEspecialidadObj(especialidad);
            doctor.setEspecialidad(especialidad.getNombre());

            boolean imagenNueva = !file.isEmpty();
            if (imagenNueva) {
                doctor.setImagen(uploadFileService.guardarImagen(file));
            }
            doctorService.guardar(doctor);
            if (imagenNueva && imagenAnterior != null) {
                uploadFileService.eliminarImagen(imagenAnterior);
            }
            redirectAttributes.addFlashAttribute("exito", "Doctor guardado correctamente.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen.");
        }
        return "redirect:/admin/doctores";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDoctor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.buscarPorId(id).orElse(null);
        if (doctor == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el doctor.");
            return "redirect:/admin/doctores";
        }
        try {
            doctorService.eliminarPorId(id);
            // La foto se borra DESPUÉS de confirmar que el doctor se eliminó.
            uploadFileService.eliminarImagen(doctor.getImagen());
            redirectAttributes.addFlashAttribute("exito", "Doctor eliminado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar: el doctor tiene citas asociadas.");
        }
        return "redirect:/admin/doctores";
    }
}