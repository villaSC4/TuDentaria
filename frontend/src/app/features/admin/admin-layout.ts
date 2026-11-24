import { Component, inject, signal, computed } from "@angular/core";
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from "@angular/router";
import { toSignal } from "@angular/core/rxjs-interop";
import { filter, map, startWith } from "rxjs";
import { AuthService } from "../../core/auth.service";
@Component({
  selector: "app-admin-layout",
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: "./admin-layout.html",
})
export class AdminLayoutComponent {
  auth = inject(AuthService);
  menu = signal(false);
  private router = inject(Router);
  private path = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url),
    ),
    { initialValue: this.router.url },
  );
  titulo = computed(() =>
    this.path().includes("/doctores")
      ? "Personal Especialista"
      : this.path().includes("/usuarios")
        ? "Seguridad y Accesos"
        : this.path().includes("/citas")
          ? "Control de Solicitudes"
          : this.path().includes("/tratamientos")
            ? "Tratamientos Clínicos"
            : "Panel de Control Clínico",
  );
  nombre = computed(() =>
    this.path() === "/admin"
      ? (this.auth.usuario()?.nombre ?? "Administrador")
      : "Administrador",
  );
}
