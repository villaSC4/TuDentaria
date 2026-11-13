import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { FieldErrorComponent } from '../../shared/field-error';
import { aplicarErrores, limpiarErroresServidor } from '../../shared/form-errors';
import { Cita, Doctor, Tratamiento, Usuario, Rol, Especialidad } from '../../core/models';
@Component({selector: 'app-admin-citas', standalone: true, imports: [CommonModule, ReactiveFormsModule, FieldErrorComponent], templateUrl: './citas.html'})
export class AdminCitasComponent {
api=inject(ApiService);fb=inject(FormBuilder);error=signal('');exito=signal('');enviando=signal(false);cargando=signal(true);editing=signal<number|null>(null);

items=signal<Cita[]>([]);estados=['PENDIENTE','ACEPTADA','CANCELADA'];
form=this.fb.nonNullable.group({estado:['PENDIENTE',Validators.required],notas:['',Validators.maxLength(1000)]});
constructor(){this.cargar();}
cargar(){this.cargando.set(true);this.api.citas().pipe(finalize(()=>this.cargando.set(false))).subscribe({next:d=>this.items.set(d),error:e=>this.error.set(aplicarErrores(this.form,e))});}
editar(c:Cita){this.editing.set(c.id);this.form.patchValue({estado:c.estado,notas:c.notas??''});}
cancelar(){this.editing.set(null);this.form.reset();}
guardar(){limpiarErroresServidor(this.form);if(this.form.invalid){this.form.markAllAsTouched();return;}const id=this.editing();if(!id)return;this.enviando.set(true);this.error.set('');this.exito.set('');this.api.actualizarCita(id,this.form.getRawValue()).pipe(finalize(()=>this.enviando.set(false))).subscribe({next:()=>{this.exito.set('Cita actualizada.');this.cancelar();this.cargar();},error:e=>this.error.set(aplicarErrores(this.form,e))});}
eliminar(c:Cita){if(!confirm('¿Eliminar la cita #'+c.id+'?'))return;this.enviando.set(true);this.error.set('');this.api.eliminarCita(c.id).pipe(finalize(()=>this.enviando.set(false))).subscribe({next:()=>{this.exito.set('Cita eliminada.');if(this.editing()===c.id)this.cancelar();this.cargar();},error:e=>this.error.set(aplicarErrores(this.form,e))});}

}
