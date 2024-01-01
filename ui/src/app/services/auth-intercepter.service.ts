import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthIntercepterService implements HttpInterceptor{

  constructor(private authService: AuthService,
    private cookieService: CookieService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(req.url.indexOf('/login') !== -1) return next.handle(req);

    const bearerToken = this.cookieService.get('token');
    return next.handle(req.clone({headers: req.headers.set('Authorization', 'Bearer ' + bearerToken)}));
  }
}
