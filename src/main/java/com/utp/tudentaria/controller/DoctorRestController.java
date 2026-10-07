package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctores")
@CrossOrigin(origins = "*")
public class DoctorRestController {

    private final DoctorService doctorService;

    public DoctorRestController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> listarTodos() {
        List<Doctor> doctores = doctorService.listarTodos();
        return ResponseEntity.ok(doctores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return doctorService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("mensaje", "Doctor no encontrado con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @PostMapping
    public ResponseEntity<Doctor> crearDoctor(@Valid @RequestBody Doctor doctor) {
        doctor.setId(null);
        doctor.setEspecialidadObj(null);
        Doctor nuevo = doctorService.guardar(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDoctor(@PathVariable Integer id, @Valid @RequestBody Doctor doctorDetails) {
        return doctorService.buscarPorId(id)
                .<ResponseEntity<?>>map(existente -> {
                     existente.setNombre(doctorDetails.getNombre());
                    // Si el texto ya no coincide con la especialidad enlazada, se suelta el enlace
                    // para que texto y relación nunca se contradigan.
                    if (existente.getEspecialidadObj() != null
                            && !existente.getEspecialidadObj().getNombre().equals(doctorDetails.getEspecialidad())) {
                        existente.setEspecialidadObj(null);
                    }
                    existente.setEspecialidad(doctorDetails.getEspecialidad());
                    if (doctorDetails.getImagen() != null) {
                        existente.setImagen(doctorDetails.getImagen());
                    }
                    Doctor actualizado = doctorService.guardar(existente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("mensaje", "No se puede actualizar. Doctor no encontrado con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDoctor(@PathVariable Integer id) {
        if (!doctorService.existePorId(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "No se puede eliminar. Doctor no encontrado con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        doctorService.eliminarPorId(id);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Doctor eliminado exitosamente");
        respuesta.put("id", id);
        return ResponseEntity.ok(respuesta);
    }
}
