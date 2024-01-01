import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { AuthService } from '../services/auth.service';
import { Route, Router } from '@angular/router';
import { formatBytes } from '../util/helpers';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  isLoggedIn = false;
  currentUser = new User();
  totalSpace: string = '';
  occupiedSpace: string = '';

  constructor(private authService: AuthService,
    private router: Router,
    private userService: UserService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if(this.isLoggedIn){
      this.currentUser = this.authService.getCurrentUser();
      this.calculateUserStorage();
    }

    this.userService.storageUpdated.subscribe({
      next: res => {
        this.authService.fetchCurrentUserDetailsAndSetLogin();

        // Will only run when fetchCurrentUserDetailsAndSetLogin() finishes executing
        this.authService.currentUserWasUpdated.subscribe({
          next: res => {
            this.currentUser = this.authService.getCurrentUser();
            this.calculateUserStorage();
          }
        })
      }
    });
  }
  
  onLogout(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  
  calculateUserStorage(){
    this.totalSpace = formatBytes(this.currentUser.assignedCloudStorage!, 1);
    this.occupiedSpace = formatBytes(this.currentUser.assignedCloudStorage! - this.currentUser.freeSpaceAvailable!, 1);
  }

}
