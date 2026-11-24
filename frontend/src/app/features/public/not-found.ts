import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
@Component({
  selector: "app-not-found",
  standalone: true,
  imports: [RouterLink],
  templateUrl: "./not-found.html",
  styles: [
    `
      .error-screen {
        background-color: #f4f7f6;
        font-family: "Segoe UI", sans-serif;
        height: 100vh;
      }
      .error-container {
        text-align: center;
        max-width: 500px;
        padding: 2rem;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
      }
      .error-code {
        font-size: 6rem;
        font-weight: 800;
        color: #1f4b61;
        line-height: 1;
      }
      .btn-home {
        background-color: #00bcd4;
        border-color: #00bcd4;
        color: white;
        transition: 0.3s;
      }
      .btn-home:hover {
        background-color: #0097a7;
        border-color: #0097a7;
        color: white;
      }
    `,
  ],
})
export class NotFoundComponent {}
