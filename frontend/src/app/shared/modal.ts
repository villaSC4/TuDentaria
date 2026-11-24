import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  Output,
  inject,
  OnChanges,
  OnDestroy,
} from "@angular/core";
import { DOCUMENT } from "@angular/common";
@Component({
  selector: "app-modal",
  standalone: true,
  template: `@if (abierto) {
    <div class="modal-backdrop fade show" (click)="cerrarSiLibre()"></div>
    <div
      class="modal fade show d-block"
      role="dialog"
      aria-modal="true"
      [attr.aria-label]="titulo"
      tabindex="-1"
      (click)="fondo($event)"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i [class]="icono + ' me-2'"></i>{{ titulo }}
            </h5>
            <button
              type="button"
              class="btn-close"
              aria-label="Cerrar"
              [disabled]="ocupado"
              (click)="cerrarSiLibre()"
            ></button>
          </div>
          <ng-content />
        </div>
      </div>
    </div>
  }`,
})
export class ModalComponent implements OnChanges, OnDestroy {
  @Input() abierto = false;
  @Input() titulo = "";
  @Input() icono = "fas fa-edit";
  @Input() ocupado = false;
  @Output() cerrar = new EventEmitter<void>();
  private document = inject(DOCUMENT);
  private element: ElementRef<HTMLElement> = inject(ElementRef);
  private previo: HTMLElement | null = null;
  private overflow: string | null = null;
  private paddingRight: string | null = null;
  ngOnChanges() {
    if (this.abierto && this.overflow === null) {
      this.previo = this.document.activeElement as HTMLElement;
      this.overflow = this.document.body.style.overflow;
      const view = this.document.defaultView;
      const scrollbar = view
        ? view.innerWidth - this.document.documentElement.clientWidth
        : 0;
      this.paddingRight = this.document.body.style.paddingRight;
      if (view && scrollbar > 0) {
        const padding =
          parseFloat(view.getComputedStyle(this.document.body).paddingRight) ||
          0;
        this.document.body.style.paddingRight = `${padding + scrollbar}px`;
      }
      this.document.body.style.overflow = "hidden";
      queueMicrotask(() =>
        this.element.nativeElement
          .querySelector<HTMLElement>(".modal")
          ?.focus(),
      );
    }
    if (!this.abierto) this.restaurar();
  }
  cerrarSiLibre() {
    if (!this.ocupado) this.cerrar.emit();
  }
  fondo(event: Event) {
    if (event.target === event.currentTarget) this.cerrarSiLibre();
  }
  @HostListener("document:keydown", ["$event"]) tecla(event: KeyboardEvent) {
    if (!this.abierto) return;
    if (event.key === "Escape") {
      event.preventDefault();
      this.cerrarSiLibre();
    }
    if (event.key === "Tab") {
      const nodes = Array.from(
        this.element.nativeElement.querySelectorAll<HTMLElement>(
          "button:not([disabled]),input:not([disabled]),select:not([disabled]),textarea:not([disabled]),a[href]",
        ),
      ).filter((e) => e.offsetParent !== null);
      const first = nodes[0],
        last = nodes[nodes.length - 1];
      if (!first) {
        event.preventDefault();
        return;
      }
      if (
        event.shiftKey &&
        (this.document.activeElement === first ||
          this.document.activeElement?.classList.contains("modal"))
      ) {
        event.preventDefault();
        last.focus();
      } else if (
        !event.shiftKey &&
        (this.document.activeElement === last ||
          this.document.activeElement?.classList.contains("modal"))
      ) {
        event.preventDefault();
        first.focus();
      }
    }
  }
  private restaurar() {
    if (this.overflow !== null) {
      this.document.body.style.overflow = this.overflow;
      this.document.body.style.paddingRight = this.paddingRight ?? "";
      this.paddingRight = null;
      this.overflow = null;
      this.previo?.isConnected && this.previo.focus();
    }
  }
  ngOnDestroy() {
    this.restaurar();
  }
}
