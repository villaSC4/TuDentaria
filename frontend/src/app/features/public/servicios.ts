import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CatalogoComponent } from './catalogo';
@Component({selector: 'app-servicios', standalone: true, imports: [CommonModule, RouterLink, CatalogoComponent], templateUrl: './servicios.html'})
export class ServiciosComponent {

}
