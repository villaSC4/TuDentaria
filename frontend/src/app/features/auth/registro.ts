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
  selector: "app-registro",
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FieldErrorComponent],
  templateUrl: "./registro.html",
})
export class RegistroComponent {
  api = inject(ApiService);
  auth = inject(AuthService);
  fb = inject(FormBuilder);
  router = inject(Router);
  mostrarPassword = signal(false);
  mostrarConfirmacion = signal(false);
  error = signal("");
  exito = signal("");
  enviando = signal(false);
  form = this.fb.nonNullable.group(
    {
      nombre: [
        "",
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      apellido: [
        "",
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      dni: ["", [Validators.required, Validators.pattern(/^\d{8}$/)]],
      telefono: ["", [Validators.required, Validators.pattern(/^\d{9}$/)]],
      email: [
        "",
        [Validators.required, Validators.email, Validators.maxLength(150)],
      ],
      password: [
        "",
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(72),
        ],
      ],
      confirmarPassword: ["", Validators.required],
    },
    {
      validators: (group: AbstractControl): ValidationErrors | null =>
        group.get("password")?.value === group.get("confirmarPassword")?.value
          ? null
          : { noCoincide: true },
    },
  );
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set("");
    this.api
      .registrar(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () =>
          void this.router.navigate(["/login"], {
            queryParams: { registrado: 1 },
          }),
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
