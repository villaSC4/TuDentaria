import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withInMemoryScrolling, withComponentInputBinding } from '@angular/router';
import { AppComponent } from './app/app';
import { routes } from './app/app.routes';
import { authInterceptor } from './app/core/auth.interceptor';
bootstrapApplication(AppComponent, { providers: [provideRouter(routes, withInMemoryScrolling({scrollPositionRestoration:'enabled', anchorScrolling:'enabled'}), withComponentInputBinding()), provideHttpClient(withInterceptors([authInterceptor]))] }).catch(console.error);
