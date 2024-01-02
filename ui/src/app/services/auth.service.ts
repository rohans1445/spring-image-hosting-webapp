import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import jwtDecode from 'jwt-decode';
import * as moment from 'moment';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../model/user.model';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, 
    private router: Router,
    private cookieService: CookieService) { }


    currentUser: User = new User();

  userHasLoggedOut = new BehaviorSubject<boolean>(false);

  // Emit an event using this subject when a users property is 
  // updated using fetchCurrentUserDetailsAndSetLogin() so that the 
  // code following fetchCurrentUserDetailsAndSetLogin() can be run 
  // sequentially.
  currentUserWasUpdated: Subject<boolean> = new Subject<boolean>();

  login(usernamePassword: object): Observable<any>{
    return this.http.post<any>(`${environment.baseUrl}/auth/login`, usernamePassword);
  }

  register(userDetails: object): Observable<any>{
    return this.http.post<any>(`${environment.baseUrl}/auth/register`, userDetails);
  }

  fetchCurrentUserDetails(): Observable<User>{
    return this.http.get<User>(`${environment.baseUrl}/user/me`);
  }

  fetchCurrentUserDetailsAndSetLogin() {
    this.fetchCurrentUserDetails().subscribe({
      next: res => {
        this.cookieService.set('currentUser', JSON.stringify(res));
        this.currentUserWasUpdated.next(true);
      }
    })
  }

  isLoggedIn(): boolean{
    if(!this.cookieService.get('token')) return false;
    return !this.isTokenExpired();
  }

  getExpiration(){
    try {
      const bearerToken = this.cookieService.get('token');
      const decodedJWT: {iat: number, sub: string, exp: number } = jwtDecode(bearerToken!);
      return moment.unix(decodedJWT.exp);
    } catch(error) {
      console.log('Error decoding token.');
      return;
    }
  }

  getCurrentUser(): User{ 
    if(this.cookieService.check('currentUser')){
      this.currentUser = JSON.parse(this.cookieService.get('currentUser')!);
      return this.currentUser;
    } else {
      console.error('Error getting current user.');
      return this.currentUser;
    }
  }
  
  isTokenExpired(){
    const bearerToken = this.cookieService.get('token');
    if(bearerToken === null){
      return true;
    }

    const decodedJWT: {iat: number, sub: string, exp: number } = jwtDecode(bearerToken);
    return moment().isAfter(moment.unix(decodedJWT.exp)); 
  }

  logout() {
    this.cookieService.delete('token');
    this.cookieService.delete('currentUser');
  }

  getAuthHeader() {
    const bearerToken = this.cookieService.get('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${bearerToken}`
    });
  }


}
