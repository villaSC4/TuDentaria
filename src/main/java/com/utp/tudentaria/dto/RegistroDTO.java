package com.utp.tudentaria.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroDTO {
    
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos.")
    private String dni;

    @NotBlank(message = "El celular es obligatorio.")
    @Pattern(regexp = "\\d{9}", message = "El celular debe tener exactamente 9 dígitos.")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Ingresa un correo válido.")
    @Size(max = 150, message = "El correo es demasiado largo.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
    private String password;

    @NotBlank(message = "Confirma tu contraseña.")
    private String confirmarPassword;

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmarPassword() { return confirmarPassword; }
    public void setConfirmarPassword(String confirmarPassword) { this.confirmarPassword = confirmarPassword; }
}
