import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ApiService } from "../../core/api.service";
import { Doctor } from "../../core/models";
@Component({
  selector: "app-equipo",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./equipo.html",
})
export class EquipoComponent {
  api = inject(ApiService);
  doctores = signal<Doctor[]>([]);
  error = signal("");
  cargando = signal(true);
  constructor() {
    this.cargar();
  }
  cargar() {
    this.cargando.set(true);
    this.error.set("");
    this.api.doctores().subscribe({
      next: (d) => {
        this.doctores.set(d);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set("No se pudo cargar el equipo.");
        this.cargando.set(false);
      },
    });
  }
  foto(d: Doctor) {
    return d.imagen
      ? "/uploads/" + encodeURIComponent(d.imagen)
      : "/img/logo.png";
  }
  fallback(e: Event) {
    const img = e.target as HTMLImageElement;
    img.src = "/img/logo.png";
  }
}
