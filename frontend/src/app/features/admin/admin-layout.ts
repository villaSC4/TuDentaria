import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { RouterOutlet } from '@angular/router';
@Component({selector: 'app-admin-layout', standalone: true, imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet], templateUrl: './admin-layout.html'})
export class AdminLayoutComponent {
links=[{path:'/admin',text:'Resumen'},{path:'/admin/doctores',text:'Doctores'},{path:'/admin/tratamientos',text:'Tratamientos'},{path:'/admin/usuarios',text:'Usuarios'},{path:'/admin/citas',text:'Citas'}];
}
