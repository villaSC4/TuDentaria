import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth.service';
@Component({selector: 'app-header', standalone: true, imports: [CommonModule, RouterLink, RouterLinkActive], templateUrl: './header.html'})
export class HeaderComponent {
auth=inject(AuthService); menu=false;
links=[{path:'/',text:'Inicio'},{path:'/nosotros',text:'Nosotros'},{path:'/servicios',text:'Servicios'},{path:'/blog',text:'Blog'},{path:'/contacto',text:'Contacto'}];
}
