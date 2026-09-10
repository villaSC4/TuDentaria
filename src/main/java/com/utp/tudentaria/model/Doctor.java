package com.utp.tudentaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "doctores")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del doctor es obligatorio.")
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria.")
    private String especialidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidadObj;

    private String imagen;

    public Doctor() {}

    public Doctor(String nombre, String especialidad, String imagen) {
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.imagen = imagen;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Especialidad getEspecialidadObj() { return especialidadObj; }
    public void setEspecialidadObj(Especialidad especialidadObj) { this.especialidadObj = especialidadObj; }
}