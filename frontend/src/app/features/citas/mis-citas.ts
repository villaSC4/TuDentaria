import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { Cita } from '../../core/models';
@Component({selector: 'app-mis-citas', standalone: true, imports: [CommonModule, RouterLink], templateUrl: './mis-citas.html'})
export class MisCitasComponent {
api=inject(ApiService);citas=signal<Cita[]>([]);error=signal('');cargando=signal(true);
constructor(){this.api.misCitas().subscribe({next:c=>{this.citas.set(c);this.cargando.set(false);},error:()=>{this.error.set('No se pudieron cargar tus citas.');this.cargando.set(false);}});}
}
