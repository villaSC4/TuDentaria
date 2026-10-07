package com.utp.tudentaria.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Respuestas de error con el mismo formato para toda la API REST. */
@RestControllerAdvice(annotations = org.springframework.web.bind.annotation.RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        Map<String, Object> cuerpo = cuerpo("Datos inválidos.");
        cuerpo.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> jsonInvalido(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo("El cuerpo o los parámetros de la petición no son válidos."));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> negocio(NegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(cuerpo("La operación viola una restricción de datos (por ejemplo, el registro tiene citas asociadas o el valor ya existe)."));
    }

    private Map<String, Object> cuerpo(String mensaje) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("mensaje", mensaje);
        return m;
    }
}