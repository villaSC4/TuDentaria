import { ModalComponent } from "../../shared/modal";
import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { finalize, forkJoin } from "rxjs";
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
  selector: "app-admin-tratamientos",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FieldErrorComponent,
    ModalComponent,
  ],
  templateUrl: "./tratamientos.html",
})
export class TratamientosComponent {
  modalAbierto = signal(false);
  nuevo() {
    this.cancelar();
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

  items = signal<Tratamiento[]>([]);
  form = this.fb.group({
    nombre: ["", [Validators.required, Validators.maxLength(100)]],
    descripcion: ["", Validators.maxLength(255)],
    precio: [
      0,
      [
        Validators.required,
        Validators.min(0.01),
        Validators.max(99999999.99),
        Validators.pattern(/^\d+(\.\d{1,2})?$/),
      ],
    ],
    duracionMinutos: [
      30,
      [Validators.required, Validators.min(1), Validators.pattern(/^\d+$/)],
    ],
  });
  constructor() {
    this.cargar();
  }
  cargar() {
    this.cargando.set(true);
    this.api
      .tratamientos()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (d) => this.items.set(d),
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
  editar(t: Tratamiento) {
    this.modalAbierto.set(true);
    this.error.set("");
    this.editing.set(t.id);
    this.form.patchValue(t);
  }
  cancelar() {
    this.modalAbierto.set(false);
    this.editing.set(null);
    this.form.reset({ precio: 0, duracionMinutos: 30 });
  }
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set("");
    this.exito.set("");
    this.api
      .guardarTratamiento(this.editing(), this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          this.exito.set("Tratamiento guardado.");
          this.cancelar();
          this.cargar();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
  eliminar(t: Tratamiento) {
    if (!confirm("¿Eliminar " + t.nombre + "?")) return;
    this.enviando.set(true);
    this.error.set("");
    this.api
      .eliminarTratamiento(t.id)
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          this.exito.set("Tratamiento eliminado.");
          if (this.editing() === t.id) this.cancelar();
          this.cargar();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
