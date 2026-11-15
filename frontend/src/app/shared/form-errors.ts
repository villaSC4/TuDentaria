import { FormGroup } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
export function aplicarErrores(form:FormGroup,error:HttpErrorResponse):string {
  const fields=error.error?.errores ?? {};
  Object.entries(fields).forEach(([name,message]) => {
    const control=form.get(name); if(control) { control.setErrors({...control.errors,servidor:String(message)}); control.markAsTouched(); }
  });
  return error.error?.mensaje ?? (error.status===0 ? 'No se pudo conectar con el servidor. Intenta nuevamente.' : error.status===403 ? 'No tienes permiso para realizar esta acción.' : 'No se pudo completar la operación.');
}
export function limpiarErroresServidor(form:FormGroup):void {
  Object.values(form.controls).forEach(control => {
    if(control.hasError('servidor')) { const errors={...control.errors}; delete errors['servidor']; control.setErrors(Object.keys(errors).length ? errors : null); }
  });
}
