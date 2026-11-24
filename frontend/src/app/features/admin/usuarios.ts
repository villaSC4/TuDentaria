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
  selector: "app-admin-usuarios",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FieldErrorComponent,
    ModalComponent,
  ],
  templateUrl: "./usuarios.html",
})
export class UsuariosComponent {
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

  auth = inject(AuthService);
  items = signal<Usuario[]>([]);
  roles = signal<Rol[]>([]);
  form = this.fb.group({
    nombre: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
    apellido: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
    email: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
    password: [
      "",
      [Validators.required, Validators.minLength(6), Validators.maxLength(72)],
    ],
    idRol: this.fb.control<number | null>(null, Validators.required),
  });
  constructor() {
    this.cargar();
  }
  cargar() {
    this.cargando.set(true);
    forkJoin({ items: this.api.usuarios(), roles: this.api.roles() })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (d) => {
          this.items.set(d.items);
          this.roles.set(d.roles);
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
  editar(u: Usuario) {
    this.modalAbierto.set(true);
    this.error.set("");
    this.editing.set(u.id);
    this.form.controls.password.setValidators([
      Validators.minLength(6),
      Validators.maxLength(72),
    ]);
    this.form.patchValue({ ...u, password: "" });
    this.form.controls.password.updateValueAndValidity();
  }
  cancelar() {
    this.modalAbierto.set(false);
    this.editing.set(null);
    this.form.reset();
    this.form.controls.password.setValidators([
      Validators.required,
      Validators.minLength(6),
      Validators.maxLength(72),
    ]);
    this.form.controls.password.updateValueAndValidity();
  }
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const self = this.editing() === this.auth.usuario()?.id;
    this.enviando.set(true);
    this.error.set("");
    this.exito.set("");
    this.api
      .guardarUsuario(this.editing(), this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          if (self) {
            this.auth.salir();
            return;
          }
          this.exito.set("Usuario guardado.");
          this.cancelar();
          this.cargar();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
  eliminar(u: Usuario) {
    if (!confirm("¿Eliminar la cuenta de " + u.email + "?")) return;
    this.enviando.set(true);
    this.error.set("");
    this.api
      .eliminarUsuario(u.id)
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          this.exito.set("Usuario eliminado.");
          if (this.editing() === u.id) this.cancelar();
          this.cargar();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
