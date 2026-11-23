import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators,
  AbstractControl,
  ValidationErrors,
} from "@angular/forms";
import { finalize } from "rxjs";
import { ApiService } from "../../core/api.service";
import { AuthService } from "../../core/auth.service";
import { FieldErrorComponent } from "../../shared/field-error";
import {
  aplicarErrores,
  limpiarErroresServidor,
} from "../../shared/form-errors";
import { Router, ActivatedRoute } from "@angular/router";
import { Doctor, Tratamiento } from "../../core/models";
import { forkJoin } from "rxjs";
@Component({
  selector: "app-cita-form",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FieldErrorComponent],
  templateUrl: "./cita-form.html",
})
export class CitaFormComponent {
  api = inject(ApiService);
  auth = inject(AuthService);
  fb = inject(FormBuilder);
  router = inject(Router);
  error = signal("");
  exito = signal("");
  enviando = signal(false);

  doctores = signal<Doctor[]>([]);
  tratamientos = signal<Tratamiento[]>([]);
  catalogoError = signal(false);
  hoy =
    new Date().getFullYear() +
    "-" +
    String(new Date().getMonth() + 1).padStart(2, "0") +
    "-" +
    String(new Date().getDate()).padStart(2, "0");
  form = this.fb.group({
    nombre: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
    apellido: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
    dni: ["", [Validators.required, Validators.pattern(/^\d{8}$/)]],
    telefono: ["", [Validators.required, Validators.pattern(/^\d{9}$/)]],
    email: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
    doctorId: this.fb.control<number | null>(null),
    tratamientoId: this.fb.control<number | null>(null),
    fecha: [
      "",
      [
        Validators.required,
        (c: AbstractControl) =>
          !c.value ||
          (c.value >= this.hoy &&
            new Date(c.value + "T12:00:00").getDay() !== 0)
            ? null
            : { fecha: true },
      ],
    ],
    hora: ["", Validators.required],
    motivo: ["", [Validators.required, Validators.maxLength(150)]],
    notas: ["", Validators.maxLength(1000)],
  });
  constructor() {
    this.cargarCatalogos();
    const u = this.auth.usuario();
    if (u?.dni) this.form.patchValue(u);
    this.form.controls.fecha.valueChanges.subscribe(() =>
      this.form.controls.hora.setValue(""),
    );
  }
  cargarCatalogos() {
    this.catalogoError.set(false);
    forkJoin({
      doctores: this.api.doctores(),
      tratamientos: this.api.tratamientos(),
    }).subscribe({
      next: (d) => {
        this.doctores.set(d.doctores);
        this.tratamientos.set(d.tratamientos);
      },
      error: () => this.catalogoError.set(true),
    });
  }
  horas() {
    const saturday =
      new Date(
        (this.form.controls.fecha.value ?? "") + "T12:00:00",
      ).getDay() === 6;
    return Array.from(
      { length: saturday ? 4 : 10 },
      (_, i) => String(i + (saturday ? 9 : 8)).padStart(2, "0") + ":00",
    );
  }
  horaTexto(h: string) {
    const hour = Number(h.slice(0, 2));
    return (
      String(hour % 12 || 12).padStart(2, "0") +
      ":00 " +
      (hour < 12 ? "AM" : "PM") +
      ([8, 13, 14, 15, 16, 17].includes(hour) ? " (L-V)" : "")
    );
  }
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.error.set("");
    this.exito.set("");
    this.enviando.set(true);
    this.api
      .solicitarCita(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: (c) => {
          this.exito.set(
            "Solicitud #" +
              c.id +
              " enviada. Nos comunicaremos contigo para confirmarla.",
          );
          this.form.reset({ doctorId: null, tratamientoId: null });
          const u = this.auth.usuario();
          if (u?.dni) this.form.patchValue(u);
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
