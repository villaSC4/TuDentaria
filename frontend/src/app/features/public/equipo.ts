import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { Doctor } from '../../core/models';
@Component({selector: 'app-equipo', standalone: true, imports: [CommonModule, RouterLink], templateUrl: './equipo.html'})
export class EquipoComponent {
api=inject(ApiService); doctores=signal<Doctor[]>([]); error=signal(''); cargando=signal(true);
constructor(){this.cargar();}
cargar(){this.cargando.set(true);this.error.set('');this.api.doctores().subscribe({next:d=>{this.doctores.set(d);this.cargando.set(false);},error:()=>{this.error.set('No se pudo cargar el equipo.');this.cargando.set(false);}});}
foto(d:Doctor){if(d.imagen==='demo-doctor-1.jpg')return '/img/team_1.jpg';if(d.imagen==='demo-doctor-2.jpg')return '/img/team_2.jpg';return d.imagen ? '/uploads/'+encodeURIComponent(d.imagen) : '/img/team_1.jpg';}
fallback(e:Event){const img=e.target as HTMLImageElement;img.onerror=null;img.src='/img/team_1.jpg';}
}
