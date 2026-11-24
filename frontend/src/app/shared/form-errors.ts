import { FormGroup } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
export function aplicarErrores(form:FormGroup,error:HttpErrorResponse):string {
  const fields=error.error?.errores ?? {};
  Object.entries(fields).forEach(([name,message]) => {
    const control=form.get(name); if(control) { control.setErrors({...control.errors,servidor:String(message)}); control.markAsTouched(); }
  });
  if (error.error?.mensaje) return error.error.mensaje;
  const proxyUnavailable = error.status === 500 && typeof error.error === 'string'
    && /proxy|ECONNREFUSED|ETIMEDOUT|ENOTFOUND/i.test(error.error);
  if (error.status === 0 || [502, 503, 504].includes(error.status) || proxyUnavailable) {
    return 'No se pudo conectar con el servidor. Verifica que el backend esté iniciado e intenta nuevamente.';
  }
  if (error.status === 404) return 'El servicio solicitado no está disponible. Verifica la configuración del servidor.';
  if (error.status === 403) return 'No tienes permiso para realizar esta acción.';
  return 'No se pudo completar la operación. Intenta nuevamente.';
}
export function limpiarErroresServidor(form:FormGroup):void {
  Object.values(form.controls).forEach(control => {
    if(control.hasError('servidor')) { const errors={...control.errors}; delete errors['servidor']; control.setErrors(Object.keys(errors).length ? errors : null); }
  });
}
