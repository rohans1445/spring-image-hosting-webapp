import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';
import { GalleryComponent } from './home/gallery/gallery.component';
import { HomeComponent } from './home/home.component';
import { UploadFormComponent } from './home/upload-form/upload-form.component';

const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: '', component: HomeComponent, children: [
    {path: 'home', component: GalleryComponent},
    {path: 'my-images', component: GalleryComponent},
    {path: 'upload', component: UploadFormComponent}
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
