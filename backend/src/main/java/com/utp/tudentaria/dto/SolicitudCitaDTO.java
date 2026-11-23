package com.utp.tudentaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class SolicitudCitaDTO {
 @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos.")
    private String dni;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "\\d{9}", message = "El celular debe tener exactamente 9 dígitos.")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Por favor, introduce un correo electrónico válido.")
    @Size(max = 150, message = "El correo es demasiado largo.")
    private String email;

    private Integer tratamientoId;

    private Integer doctorId;

    @NotNull(message = "Elige una fecha.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "La fecha de la cita no puede ser en el pasado.")
    private LocalDate fecha;

    @NotNull(message = "Elige una hora.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime hora;

    @NotBlank(message = "El motivo de la visita es obligatorio.")
    @Size(max = 150, message = "El motivo no puede exceder los 150 caracteres.")
    private String motivo;

    @Size(max = 1000, message = "Las notas no pueden exceder los 1000 caracteres.")
    private String notas;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getTratamientoId() { return tratamientoId; }
    public void setTratamientoId(Integer tratamientoId) { this.tratamientoId = tratamientoId; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}