package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Cita;
import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.repository.PacienteRepository;
import com.utp.tudentaria.service.CitaService;
import com.utp.tudentaria.service.DoctorService;
import com.utp.tudentaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PublicController {

    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final DoctorService doctorService;
    private final PacienteRepository pacienteRepository; // <-- Nuevo

    // Constructor actualizado
    public PublicController(UsuarioService usuarioService, CitaService citaService, 
                            DoctorService doctorService, PacienteRepository pacienteRepository) {
        this.usuarioService = usuarioService;
        this.citaService = citaService;
        this.doctorService = doctorService;
        this.pacienteRepository = pacienteRepository; // <-- Nuevo
    }

    @GetMapping("/")
    public String inicio(Model model) {
        if (!model.containsAttribute("cita")) {
            model.addAttribute("cita", new Cita());
        }
        return "index";
    }

@PostMapping("/solicitar-cita")
    public String procesarCita(@Valid @ModelAttribute("cita") Cita cita,
                               BindingResult result,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        String referer = request.getHeader("Referer");
        boolean esContacto = referer != null && referer.contains("/contacto");

        if (result.hasErrors()) {
            model.addAttribute("cita", cita);
            model.addAttribute("errorCita", "Por favor, corrige los errores en el formulario de solicitud.");

            if (esContacto) {
                return "pages/contacto";
            }
            return "index";
        }
        
        // 1. Busca si el paciente ya existe por su correo, si no, lo crea y lo guarda en la BD
        Paciente paciente = pacienteRepository.findByEmail(cita.getEmail())
                .orElseGet(() -> {
                    Paciente nuevoPaciente = new Paciente(
                            cita.getNombre(), 
                            cita.getApellido(), 
                            null, // DNI puede quedar nulo inicialmente
                            cita.getTelephone(), 
                            cita.getEmail()
                    );
                    return pacienteRepository.save(nuevoPaciente);
                });

        // 2. Vincula el paciente encontrado o creado a la cita actual
        cita.setPaciente(paciente);
        
        // ==========================================

        // 3. Finalmente, guarda la cita (ahora sí se guardará con el paciente_id)
        citaService.guardar(cita);
        
        redirectAttributes.addFlashAttribute("exitoCita", "Tu solicitud de cita ha sido enviada con éxito. Nos comunicaremos contigo pronto.");

        if (esContacto) {
            return "redirect:/contacto";
        }
        return "redirect:/#contacto";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("doctores", doctorService.listarTodos());
        return "pages/nosotros";
    }

    @GetMapping("/servicios")
    public String servicios() { return "pages/servicios"; }

    @GetMapping("/blog")
    public String blog() { return "pages/blog"; }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        if (!model.containsAttribute("cita")) {
            model.addAttribute("cita", new Cita());
        }
        return "pages/contacto";
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "pages/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
        return "redirect:/login?registrado";
    }
}