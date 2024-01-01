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

  deleteImage(id: number){
    return this.http.delete<void>(`${environment.baseUrl}/user/me/images/${id}`);
  }
  
  updateImage(id:number, visibility: string){
    return this.http.put<void>(`${environment.baseUrl}/user/me/images/${id}`, {visibility: visibility});
  }


}
