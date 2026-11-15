export interface Usuario { id:number; nombre:string; apellido:string; email:string; dni:string; telefono:string; roles:string[]; idRol:number; }
export interface LoginResponse { accessToken:string; tokenType:string; expiresIn:number; usuario:Usuario; }
export interface Doctor { id:number; nombre:string; especialidad:string; imagen:string|null; especialidadObj?:{id:number; nombre?:string}|null; }
export interface Tratamiento { id:number; nombre:string; descripcion:string; precio:number; duracionMinutos:number; }
export interface Cita { id:number; nombre:string; apellido:string; email:string; telefono:string; fecha:string; hora:string; motivo:string; notas:string; estado:string; doctor:string; tratamiento:string; }
export interface Rol { id:number; nombre:string; }
export interface Especialidad { id:number; nombre:string; descripcion:string; }
