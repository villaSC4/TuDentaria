import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
export const authInterceptor:HttpInterceptorFn=(request,next) => {
  const auth=inject(AuthService);
  const api=request.url.startsWith('/api/');
  const publicAuth=request.url.startsWith('/api/auth/');
  const token=api && !publicAuth ? auth.token() : null;
  return next(token ? request.clone({setHeaders:{Authorization:`Bearer ${token}`}}) : request).pipe(catchError((error:HttpErrorResponse) => {
    if (api && !publicAuth && error.status===401) auth.salir();
    return throwError(() => error);
  }));
};
