import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { LoginResponse, Usuario } from './models';
@Injectable({providedIn:'root'})
export class AuthService {
  private http=inject(HttpClient);
  private router=inject(Router);
  readonly usuario=signal<Usuario|null>(null);
  readonly esAdmin=computed(() => this.usuario()?.roles.includes('ROLE_ADMIN') ?? false);
  readonly autenticado=computed(() => !!this.usuario() && !!this.token());
  constructor() {
    try {
      const saved=JSON.parse(sessionStorage.getItem('tudentaria.auth') ?? 'null');
      if (saved && saved.expiresAt > Date.now()) this.usuario.set(saved.usuario);
      else sessionStorage.removeItem('tudentaria.auth');
    } catch { sessionStorage.removeItem('tudentaria.auth'); }
  }
  token():string|null {
    try { const saved=JSON.parse(sessionStorage.getItem('tudentaria.auth') ?? 'null'); return saved?.expiresAt > Date.now() ? saved.accessToken : null; }
    catch { return null; }
  }
  login(data:{email:string;password:string}) {
    return this.http.post<LoginResponse>('/api/auth/login',data).pipe(tap(result => {
      sessionStorage.setItem('tudentaria.auth', JSON.stringify({...result, expiresAt:Date.now()+result.expiresIn*1000}));
      this.usuario.set(result.usuario);
    }));
  }
  actualizarUsuario(usuario:Usuario) {
    this.usuario.set(usuario);
    const saved=JSON.parse(sessionStorage.getItem('tudentaria.auth') ?? 'null');
    if(saved) sessionStorage.setItem('tudentaria.auth',JSON.stringify({...saved,usuario}));
  }
  salir(redirect=true) { sessionStorage.removeItem('tudentaria.auth'); this.usuario.set(null); if(redirect) void this.router.navigateByUrl('/login'); }
}
