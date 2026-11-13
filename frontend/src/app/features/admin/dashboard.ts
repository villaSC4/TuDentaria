import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { FieldErrorComponent } from '../../shared/field-error';
import { aplicarErrores, limpiarErroresServidor } from '../../shared/form-errors';
import { Cita, Doctor, Tratamiento, Usuario, Rol, Especialidad } from '../../core/models';
@Component({selector: 'app-dashboard', standalone: true, imports: [CommonModule], templateUrl: './dashboard.html'})
export class DashboardComponent {
api=inject(ApiService);resumen=signal<Record<string,number>>({});error=signal('');cargando=signal(true);
constructor(){this.api.resumen().subscribe({next:r=>{this.resumen.set(r);this.cargando.set(false);},error:()=>{this.error.set('No se pudo cargar el resumen.');this.cargando.set(false);}});}
}
