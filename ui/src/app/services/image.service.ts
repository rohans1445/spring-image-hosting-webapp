import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { Image } from '../model/image.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private http: HttpClient,
    private auth: AuthService) { }

    getAllImages(){
      return this.http.get<Image[]>(`${environment.baseUrl}/images`);
    }

}
