import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header';
import { FooterComponent } from './shared/footer';
@Component({selector:'app-root',standalone:true,imports:[RouterOutlet,HeaderComponent,FooterComponent],template:`<app-header/><main id="contenido"><router-outlet/></main><app-footer/>`})
export class AppComponent {}
