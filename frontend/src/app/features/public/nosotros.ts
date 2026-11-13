import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EquipoComponent } from './equipo';
@Component({selector: 'app-nosotros', standalone: true, imports: [CommonModule, EquipoComponent], templateUrl: './nosotros.html'})
export class NosotrosComponent {

}
