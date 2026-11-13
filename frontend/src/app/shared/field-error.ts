import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';
@Component({selector:'app-field-error',standalone:true,template:`@if(control?.touched && control?.invalid) { <span class="field-error" role="alert">{{ mensaje() }}</span> }`})
export class FieldErrorComponent {
  @Input() control:AbstractControl|null=null;
  mensaje():string {
    const e=this.control?.errors ?? {};
    if(e['servidor']) return e['servidor'];
    if(e['required']) return 'Este campo es obligatorio.';
    if(e['email']) return 'Ingresa un correo válido.';
    if(e['minlength']) return `Ingresa al menos ${e['minlength'].requiredLength} caracteres.`;
    if(e['maxlength']) return `Máximo ${e['maxlength'].requiredLength} caracteres.`;
    if(e['pattern']) return 'El formato no es válido.';
    if(e['min']) return `El valor mínimo es ${e['min'].min}.`;
    if(e['fecha']) return 'Selecciona una fecha desde hoy, de lunes a sábado.';
    if(e['archivo']) return 'Selecciona una imagen JPG, PNG o WEBP de hasta 5 MB.';
    return 'Revisa el valor ingresado.';
  }
}
