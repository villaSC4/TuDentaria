import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
@Component({
  selector: "app-servicios",
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: "./servicios.html",
})
export class ServiciosComponent {}
