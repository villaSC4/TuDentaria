import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
export const authGuard:CanActivateFn=(_,state) => {
  const auth=inject(AuthService); const router=inject(Router);
  return auth.autenticado() || router.createUrlTree(['/login'],{queryParams:{returnUrl:state.url}});
};
export const adminGuard:CanActivateFn=(_,state) => {
  const auth=inject(AuthService); const router=inject(Router);
  return !auth.autenticado() ? router.createUrlTree(['/login'],{queryParams:{returnUrl:state.url}}) : auth.esAdmin() || router.createUrlTree(['/403']);
};
