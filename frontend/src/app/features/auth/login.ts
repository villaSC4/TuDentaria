import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
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
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FieldErrorComponent],
  templateUrl: "./login.html",
})
export class LoginComponent {
  api = inject(ApiService);
  auth = inject(AuthService);
  fb = inject(FormBuilder);
  router = inject(Router);
  mostrarPassword = signal(false);
  mostrarConfirmacion = signal(false);
  error = signal("");
  exito = signal("");
  enviando = signal(false);
  route = inject(ActivatedRoute);
  form = this.fb.nonNullable.group({
    email: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
    password: ["", [Validators.required, Validators.maxLength(72)]],
  });
  constructor() {
    if (this.route.snapshot.queryParamMap.has("registrado"))
      this.exito.set("Cuenta creada. Ya puedes ingresar.");
  }
  guardar() {
    limpiarErroresServidor(this.form);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set("");
    this.auth
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          const target = this.route.snapshot.queryParamMap.get("returnUrl");
          void this.router.navigateByUrl(
            target?.startsWith("/") && !target.startsWith("//")
              ? target
              : this.auth.esAdmin()
                ? "/admin"
                : "/perfil",
          );
        },
        error: (e) => this.error.set(aplicarErrores(this.form, e)),
      });
  }
}
