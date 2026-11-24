import { RouterLink } from "@angular/router";
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
@Component({
  selector: "app-perfil",
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FieldErrorComponent],
  templateUrl: "./perfil.html",
})
export class PerfilComponent {
  api = inject(ApiService);
  auth = inject(AuthService);
  fb = inject(FormBuilder);
  router = inject(Router);
  error = signal("");
  exito = signal("");
  enviando = signal(false);

  cargando = signal(true);
  form = this.fb.nonNullable.group({
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
    telefono: ["", Validators.pattern(/^$|\d{9}$/)],
  });
  constructor() {
    this.form.disable();
    this.api
      .perfil()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (u) => {
          this.form.patchValue(u);
          this.auth.actualizarUsuario(u);
          this.form.enable();
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
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
      .guardarPerfil(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: (r) => {
          if (r.requiereLogin) {
            this.auth.salir();
          } else {
            this.exito.set(r.mensaje);
            const u = this.auth.usuario();
            if (u)
              this.auth.actualizarUsuario({ ...u, ...this.form.getRawValue() });
          }
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
