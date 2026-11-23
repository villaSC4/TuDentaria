package com.utp.tudentaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones_blog")
public class PublicacionBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El título de la publicación es obligatorio.")
    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(length = 255)
    private String imagenUrl;

    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Doctor autor;

    public PublicacionBlog() {}

    public PublicacionBlog(String titulo, String contenido, String imagenUrl, Doctor autor) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.imagenUrl = imagenUrl;
        this.autor = autor;
        this.fechaPublicacion = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public Doctor getAutor() { return autor; }
    public void setAutor(Doctor autor) { this.autor = autor; }
}
