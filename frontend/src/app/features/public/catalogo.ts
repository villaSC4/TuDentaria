import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { Tratamiento } from '../../core/models';
@Component({selector: 'app-catalogo', standalone: true, imports: [CommonModule, RouterLink], templateUrl: './catalogo.html'})
export class CatalogoComponent {
api=inject(ApiService);items=signal<Tratamiento[]>([]);error=signal('');cargando=signal(true);
constructor(){this.cargar();}
cargar(){this.error.set('');this.cargando.set(true);this.api.tratamientos().subscribe({next:d=>{this.items.set(d);this.cargando.set(false);},error:()=>{this.error.set('No se pudo cargar el catálogo.');this.cargando.set(false);}});}
}
