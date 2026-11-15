import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CitaFormComponent } from './cita-form';
@Component({selector: 'app-contacto', standalone: true, imports: [CommonModule, CitaFormComponent], templateUrl: './contacto.html'})
export class ContactoComponent {

}
