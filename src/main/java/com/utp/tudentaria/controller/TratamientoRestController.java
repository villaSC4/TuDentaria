package com.utp.tudentaria.controller;

import com.utp.tudentaria.model.Tratamiento;
import com.utp.tudentaria.service.TratamientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tratamientos")
@CrossOrigin(origins = "*")
public class TratamientoRestController {

    private final TratamientoService tratamientoService;

    public TratamientoRestController(TratamientoService tratamientoService) {
        this.tratamientoService = tratamientoService;
    }

    // 1. LEER TODOS (READ ALL)
    @GetMapping
    public ResponseEntity<List<Tratamiento>> listarTodos() {
        return ResponseEntity.ok(tratamientoService.listarTodos());
    }

    // 2. LEER POR ID (READ BY ID)
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return tratamientoService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("mensaje", "Tratamiento no encontrado con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    // 3. CREAR (CREATE)
    @PostMapping
    public ResponseEntity<Tratamiento> crearTratamiento(@Valid @RequestBody Tratamiento tratamiento) {
        tratamiento.setId(null);
        Tratamiento nuevo = tratamientoService.guardar(tratamiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // 4. ACTUALIZAR (UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTratamiento(@PathVariable Integer id, @Valid @RequestBody Tratamiento details) {
        return tratamientoService.buscarPorId(id)
                .<ResponseEntity<?>>map(existente -> {
                    existente.setNombre(details.getNombre());
                    existente.setDescripcion(details.getDescripcion());
                    existente.setPrecio(details.getPrecio());
                    existente.setDuracionMinutos(details.getDuracionMinutos());
                    Tratamiento actualizado = tratamientoService.guardar(existente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("mensaje", "No se puede actualizar. Tratamiento no encontrado con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    // 5. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTratamiento(@PathVariable Integer id) {
        if (!tratamientoService.existePorId(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "No se puede eliminar. Tratamiento no encontrado con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        tratamientoService.eliminarPorId(id);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Tratamiento eliminado exitosamente");
        respuesta.put("id", id);
        return ResponseEntity.ok(respuesta);
    }
}
