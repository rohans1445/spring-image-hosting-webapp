import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  registrationForm: FormGroup = new FormGroup({});
  isLoading: boolean = false;
  isError: boolean = false;

  constructor(private authService: AuthService,
    private cookieService: CookieService,
    private router: Router) { }

  ngOnInit(): void {
    this.registrationForm = new FormGroup({
      username: new FormControl('', Validators.required),
      email: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    })
  }

  onSubmit(){
    this.isLoading = true;
    const formValue = this.registrationForm.value;

    this.authService.register(
      {
        username: formValue.username,
        password: formValue.password,
        email: formValue.email
      }
    ).subscribe({
      next: res => {
        this.authService.login({
          username: formValue.username, 
          password: formValue.password
        }).subscribe({
          next: res => {
            this.isLoading = false;
            this.cookieService.set('token', res.token);
    
            this.authService.fetchCurrentUserDetails().subscribe((res) => {
              this.cookieService.set('currentUser', JSON.stringify(res));
              this.router.navigate(['/home']);
            });
    
          },
          error: res => {
            this.isLoading = false;
            this.isError = true;
          }
        })
      },
      error: res => {
        this.isError = true;
        this.isLoading = false;
      }
    })

  }

}
