import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Image } from '../model/image.model';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  storageUpdated: Subject<boolean> = new Subject<boolean>();

  constructor(private http: HttpClient) { }

  getUserImages(){
    return this.http.get<Image[]>(`${environment.baseUrl}/user/me/images`);
  }


}
