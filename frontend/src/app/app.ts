import { Component, computed, inject, signal, effect } from "@angular/core";
import { NavigationEnd, Router, RouterOutlet } from "@angular/router";
import { toSignal } from "@angular/core/rxjs-interop";
import { filter, map, startWith } from "rxjs";
import { HeaderComponent } from "./shared/header";
import { FooterComponent } from "./shared/footer";
@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  template: ` @if (admin()) {
      <link
        rel="stylesheet"
        href="/css/admin.css"
        (load)="styleReady.set(true)"
      />
    } @else if (!errorPage()) {
      <link
        rel="stylesheet"
        href="/css/styles.css"
        (load)="styleReady.set(true)"
      />
    }
    @if (publicPage()) {
      <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&family=Raleway:wght@400;600;700&display=swap"
      />
    }
    <div
      class="app-shell"
      [style.visibility]="styleReady() ? null : 'hidden'"
      [class.auth-shell]="authPage()"
      [class.profile-shell]="profilePage()"
      [class.admin-shell]="admin()"
      [class.error-shell]="errorPage()"
    >
      @if (publicPage()) {
        <app-header />
      }
      <main id="contenido"><router-outlet /></main>
      @if (publicPage()) {
        <app-footer />
      }
    </div>`,
})
export class AppComponent {
  styleReady = signal(false);
  private router = inject(Router);
  private currentUrl = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url),
    ),
    { initialValue: this.router.url },
  );
  path = computed(() => this.currentUrl().split(/[?#]/)[0]);
  admin = computed(
    () => this.path() === "/admin" || this.path().startsWith("/admin/"),
  );
  authPage = computed(() => ["/login", "/registro"].includes(this.path()));
  profilePage = computed(() => ["/perfil", "/mis-citas"].includes(this.path()));
  publicPage = computed(() =>
    ["/", "/nosotros", "/servicios", "/blog", "/contacto"].includes(
      this.path(),
    ),
  );
  private theme = computed(() =>
    this.admin() ? "admin" : this.errorPage() ? "error" : "public",
  );
  constructor() {
    effect(() => {
      this.theme();
      this.styleReady.set(this.errorPage());
    });
  }
  errorPage = computed(
    () =>
      !this.admin() &&
      !this.authPage() &&
      !this.profilePage() &&
      !this.publicPage(),
  );
}
