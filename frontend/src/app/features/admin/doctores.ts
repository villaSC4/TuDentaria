import { ModalComponent } from "../../shared/modal";
import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { finalize, forkJoin, switchMap, of } from "rxjs";
import { ApiService } from "../../core/api.service";
import { AuthService } from "../../core/auth.service";
import { FieldErrorComponent } from "../../shared/field-error";
import {
  aplicarErrores,
  limpiarErroresServidor,
} from "../../shared/form-errors";
import {
  Cita,
  Doctor,
  Tratamiento,
  Usuario,
  Rol,
  Especialidad,
} from "../../core/models";
@Component({
  selector: "app-admin-doctores",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FieldErrorComponent,
    ModalComponent,
  ],
  templateUrl: "./doctores.html",
})
export class DoctoresComponent {
  modalAbierto = signal(false);
  nuevo() {
    this.cancelar();
    this.form.controls.foto.setValidators(Validators.required);
    this.form.controls.foto.updateValueAndValidity();
    this.error.set("");
    this.modalAbierto.set(true);
  }
  api = inject(ApiService);
  fb = inject(FormBuilder);
  error = signal("");
  exito = signal("");
  enviando = signal(false);
  cargando = signal(true);
  editing = signal<number | null>(null);

  items = signal<Doctor[]>([]);
  especialidades = signal<Especialidad[]>([]);
  file: File | null = null;
  form = this.fb.group({
    nombre: ["", [Validators.required, Validators.maxLength(100)]],
    especialidadId: this.fb.control<number | null>(null, Validators.required),
    foto: this.fb.control<File | null>(null),
  });
  constructor() {
    this.cargar();
  }
  cargar() {
    this.cargando.set(true);
    forkJoin({
      items: this.api.doctores(),
      especialidades: this.api.especialidades(),
    })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (d) => {
          this.items.set(d.items);
          this.especialidades.set(d.especialidades);
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
  archivo(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0] ?? null;
    this.file = file;
    this.form.controls.foto.setValue(file);
    if (
      file &&
      (!["image/jpeg", "image/png", "image/webp"].includes(file.type) ||
        file.size > 5 * 1024 * 1024)
    ) {
      this.form.controls.foto.setErrors({ archivo: true });
      this.form.controls.foto.markAsTouched();
    }
  }
  editar(d: Doctor) {
    this.cancelar();
    this.form.controls.foto.clearValidators();
    this.form.controls.foto.updateValueAndValidity();
    this.modalAbierto.set(true);
    this.error.set("");
    this.editing.set(d.id);
    this.form.patchValue({
      nombre: d.nombre,
      especialidadId: d.especialidadObj?.id ?? null,
    });
  }
  cancelar() {
    this.modalAbierto.set(false);
    this.editing.set(null);
    this.form.reset();
    this.file = null;
    const input = document.getElementById("foto") as HTMLInputElement | null;
    if (input) input.value = "";
  }
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const especialidad = this.especialidades().find(
      (e) => e.id === value.especialidadId,
    );
    const file = this.file;
    this.enviando.set(true);
    this.error.set("");
    this.exito.set("");
    this.api
      .guardarDoctor(this.editing(), {
        nombre: value.nombre,
        especialidad: especialidad?.nombre,
        especialidadObj: { id: value.especialidadId },
      })
      .pipe(
        switchMap((result) => {
          // Si falla la foto, el siguiente intento edita el doctor ya creado.
          this.editing.set(result.id);
          return file ? this.api.fotoDoctor(result.id, file) : of(result);
        }),
        finalize(() => this.enviando.set(false)),
      )
      .subscribe({
        next: () => {
          this.exito.set("Doctor guardado.");
          this.cancelar();
          this.cargar();
        },
        error: (e) => {
          this.error.set(aplicarErrores(this.form, e));
          this.cargar();
        },
      });
  }

  fotoUrl(d: Doctor) {
    return d.imagen
      ? "/uploads/" + encodeURIComponent(d.imagen)
      : "/img/logo.png";
  }
  imagenError(event: Event) {
    (event.target as HTMLImageElement).src = "/img/logo.png";
  }
  eliminar(d: Doctor) {
    if (!confirm("¿Eliminar a " + d.nombre + "?")) return;
    this.enviando.set(true);
    this.error.set("");
    this.api
      .eliminarDoctor(d.id)
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          this.exito.set("Doctor eliminado.");
          if (this.editing() === d.id) this.cancelar();
          this.cargar();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
