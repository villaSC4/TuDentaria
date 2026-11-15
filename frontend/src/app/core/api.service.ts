import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cita, Doctor, Tratamiento, Usuario, Rol, Especialidad } from './models';
@Injectable({providedIn:'root'})
export class ApiService {
  private http = inject(HttpClient);
  doctores = () => this.http.get<Doctor[]>('/api/doctores');
  tratamientos = () => this.http.get<Tratamiento[]>('/api/tratamientos');
  especialidades = () => this.http.get<Especialidad[]>('/api/especialidades');
  registrar = (data:unknown) => this.http.post<Usuario>('/api/auth/registro',data);
  solicitarCita = (data:unknown) => this.http.post<Cita>('/api/citas',data);
  misCitas = () => this.http.get<Cita[]>('/api/citas/mias');
  perfil = () => this.http.get<Usuario>('/api/perfil');
  guardarPerfil = (data:unknown) => this.http.put<{mensaje:string; requiereLogin:boolean}>('/api/perfil',data);
  resumen = () => this.http.get<Record<string,number>>('/api/admin/resumen');
  usuarios = () => this.http.get<Usuario[]>('/api/admin/usuarios');
  roles = () => this.http.get<Rol[]>('/api/admin/roles');
  guardarUsuario = (id:number|null,data:unknown) => id ? this.http.put('/api/admin/usuarios/'+id,data) : this.http.post('/api/admin/usuarios',data);
  eliminarUsuario = (id:number) => this.http.delete('/api/admin/usuarios/'+id);
  citas = () => this.http.get<Cita[]>('/api/admin/citas');
  actualizarCita = (id:number,data:unknown) => this.http.patch<Cita>('/api/admin/citas/'+id,data);
  eliminarCita = (id:number) => this.http.delete('/api/admin/citas/'+id);
  guardarDoctor = (id:number|null,data:unknown) => id ? this.http.put<Doctor>('/api/doctores/'+id,data) : this.http.post<Doctor>('/api/doctores',data);
  eliminarDoctor = (id:number) => this.http.delete('/api/doctores/'+id);
  fotoDoctor = (id:number,file:File) => { const data=new FormData(); data.append('file',file); return this.http.post<Doctor>('/api/admin/doctores/'+id+'/foto',data); };
  guardarTratamiento = (id:number|null,data:unknown) => id ? this.http.put('/api/tratamientos/'+id,data) : this.http.post('/api/tratamientos',data);
  eliminarTratamiento = (id:number) => this.http.delete('/api/tratamientos/'+id);
}
