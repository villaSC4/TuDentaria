package com.utp.tudentaria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del tratamiento es obligatorio.")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio.")
    @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 2 decimales.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    private Integer duracionMinutos;

    @OneToMany(mappedBy = "tratamiento", fetch = FetchType.LAZY)

    @JsonIgnore
    private List<Cita> citas = new ArrayList<>();

    public Tratamiento() {}

    public Tratamiento(String nombre, String descripcion, BigDecimal precio, Integer duracionMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }
}
