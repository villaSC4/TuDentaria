import { Component, HostListener } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-footer",
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: "./footer.html",
})
export class FooterComponent {
  visible = false;
  @HostListener("window:scroll") scroll() {
    this.visible = window.scrollY > 300;
  }
  subir(event: Event) {
    event.preventDefault();
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
}
