package com.utp.tudentaria.controller;

import com.utp.tudentaria.dto.RegistroDTO;
import com.utp.tudentaria.dto.SolicitudCitaDTO;
import com.utp.tudentaria.exception.NegocioException;
import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.model.Paciente;
import com.utp.tudentaria.model.Tratamiento;
import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.service.CitaService;
import com.utp.tudentaria.service.DoctorService;
import com.utp.tudentaria.service.TratamientoService;
import com.utp.tudentaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PublicController {

    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final DoctorService doctorService;
    private final TratamientoService tratamientoService;

    public PublicController(UsuarioService usuarioService,
                            CitaService citaService,
                            DoctorService doctorService,
                            TratamientoService tratamientoService) {
        this.usuarioService = usuarioService;
        this.citaService = citaService;
        this.doctorService = doctorService;
        this.tratamientoService = tratamientoService;
    }

    // ---------- Datos comunes a todas las vistas públicas ----------

    @ModelAttribute("doctores")
    public List<Doctor> doctores() {
        return doctorService.listarTodos();
    }

    @ModelAttribute("tratamientos")
    public List<Tratamiento> tratamientos() {
        return tratamientoService.listarTodos();
    }

    @ModelAttribute("pacienteLogueado")
    public boolean pacienteLogueado(Authentication auth) {
        return pacienteDe(auth) != null;
    }

    // ---------- Páginas ----------

    @GetMapping("/")
    public String inicio(Model model, Authentication auth) {
        prepararSolicitud(model, auth);
        return "index";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, Authentication auth) {
        prepararSolicitud(model, auth);
        return "pages/contacto";
    }

    @GetMapping("/nosotros")
    public String nosotros() { return "pages/nosotros"; }

    @GetMapping("/servicios")
    public String servicios() { return "pages/servicios"; }

    @GetMapping("/blog")
    public String blog() { return "pages/blog"; }

    @GetMapping("/login")
    public String login() { return "pages/login"; }

    // ---------- Solicitud de cita ----------

    @PostMapping("/solicitar-cita")
    public String procesarCita(@Valid @ModelAttribute("solicitud") SolicitudCitaDTO solicitud,
                               BindingResult result,
                               @RequestParam(value = "origen", defaultValue = "inicio") String origen,
                               Authentication auth,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        boolean desdeContacto = "contacto".equals(origen);   // solo valores conocidos, nunca el Referer
        Paciente logueado = pacienteDe(auth);

        if (!result.hasErrors()) {
            try {
                citaService.solicitar(solicitud, logueado);
            } catch (NegocioException e) {
                aplicarError(result, e);
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("errorCita", "Por favor, corrige los errores en el formulario de solicitud.");
            return desdeContacto ? "pages/contacto" : "index";
        }

        redirectAttributes.addFlashAttribute("exitoCita",
                "Tu solicitud de cita ha sido enviada con éxito. Nos comunicaremos contigo pronto.");
        return desdeContacto ? "redirect:/contacto" : "redirect:/#contacto";
    }

    // ---------- Registro ----------

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        return "pages/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("registro") RegistroDTO registro,
                                   BindingResult result) {
        if (registro.getPassword() != null && !registro.getPassword().equals(registro.getConfirmarPassword())) {
            result.rejectValue("confirmarPassword", "noCoincide", "Las contraseñas no coinciden.");
        }
        if (!result.hasErrors()) {
            try {
                usuarioService.registrar(registro);
            } catch (NegocioException e) {
                aplicarError(result, e);
            }
        }
        if (result.hasErrors()) {
            return "pages/registro";
        }
        return "redirect:/login?registrado";
    }

    // ---------- Utilidades ----------

    private void prepararSolicitud(Model model, Authentication auth) {
        if (model.containsAttribute("solicitud")) {
            return;
        }
        SolicitudCitaDTO dto = new SolicitudCitaDTO();
        Paciente p = pacienteDe(auth);
        if (p != null) {
            dto.setNombre(p.getNombre());
            dto.setApellido(p.getApellido());
            dto.setDni(p.getDni());
            dto.setTelefono(p.getTelefono());
            dto.setEmail(p.getEmail());
        }
        model.addAttribute("solicitud", dto);
    }

    private Paciente pacienteDe(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return null;
        }
        return usuarioService.buscarPorEmail(auth.getName()).map(Usuario::getPaciente).orElse(null);
    }

    private void aplicarError(BindingResult result, NegocioException e) {
        if (e.getCampo() != null) {
            result.rejectValue(e.getCampo(), "negocio", e.getMessage());
        } else {
            result.reject("negocio", e.getMessage());
        }
    }
}