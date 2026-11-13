import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/auth.guard';
export const routes:Routes=[
 {path:'',title:'TuDentaria | Inicio',loadComponent:()=>import('./features/public/inicio').then(m=>m.InicioComponent)},
 {path:'nosotros',title:'Nosotros | TuDentaria',loadComponent:()=>import('./features/public/nosotros').then(m=>m.NosotrosComponent)},
 {path:'servicios',title:'Servicios | TuDentaria',loadComponent:()=>import('./features/public/servicios').then(m=>m.ServiciosComponent)},
 {path:'blog',title:'Blog | TuDentaria',loadComponent:()=>import('./features/public/blog').then(m=>m.BlogComponent)},
 {path:'contacto',title:'Contacto | TuDentaria',loadComponent:()=>import('./features/citas/contacto').then(m=>m.ContactoComponent)},
 {path:'login',title:'Ingresar | TuDentaria',loadComponent:()=>import('./features/auth/login').then(m=>m.LoginComponent)},
 {path:'registro',title:'Registro | TuDentaria',loadComponent:()=>import('./features/auth/registro').then(m=>m.RegistroComponent)},
 {path:'perfil',title:'Mi perfil | TuDentaria',canActivate:[authGuard],loadComponent:()=>import('./features/perfil/perfil').then(m=>m.PerfilComponent)},
 {path:'mis-citas',title:'Mis citas | TuDentaria',canActivate:[authGuard],loadComponent:()=>import('./features/citas/mis-citas').then(m=>m.MisCitasComponent)},
 {path:'admin',canActivate:[adminGuard],loadComponent:()=>import('./features/admin/admin-layout').then(m=>m.AdminLayoutComponent),children:[
  {path:'',title:'Administración | TuDentaria',loadComponent:()=>import('./features/admin/dashboard').then(m=>m.DashboardComponent)},
  {path:'doctores',title:'Doctores | TuDentaria',loadComponent:()=>import('./features/admin/doctores').then(m=>m.DoctoresComponent)},
  {path:'tratamientos',title:'Tratamientos | TuDentaria',loadComponent:()=>import('./features/admin/tratamientos').then(m=>m.TratamientosComponent)},
  {path:'usuarios',title:'Usuarios | TuDentaria',loadComponent:()=>import('./features/admin/usuarios').then(m=>m.UsuariosComponent)},
  {path:'citas',title:'Citas | TuDentaria',loadComponent:()=>import('./features/admin/citas').then(m=>m.AdminCitasComponent)}]},
 {path:'403',title:'Acceso restringido | TuDentaria',loadComponent:()=>import('./features/public/forbidden').then(m=>m.ForbiddenComponent)},
 {path:'**',title:'Página no encontrada | TuDentaria',loadComponent:()=>import('./features/public/not-found').then(m=>m.NotFoundComponent)}
];
