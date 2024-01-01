import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../model/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  isLoading = false;
  isError = false;
  loginForm: FormGroup = new FormGroup({});

  constructor(private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    })
  }

  onSubmit(){
    this.isLoading = true;
    const formValue = this.loginForm.value;

    this.authService.login({
      username: formValue.username, 
      password: formValue.password
    }).subscribe({
      next: res => {
        this.isLoading = false;
        localStorage.setItem('token', res.token);
        
        this.authService.fetchCurrentUserDetailsAndSetLogin();
        this.router.navigate(['/home']);

      },
      error: res => {
        this.isLoading = false;
        this.isError = true;
      }
    })
  }

}
