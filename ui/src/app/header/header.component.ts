import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { AuthService } from '../services/auth.service';
import { Route, Router } from '@angular/router';
import { formatBytes } from '../util/helpers';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  isLoggedIn = false;
  currentUser = new User();
  freeSpace: string = '';
  occupiedSpace: string = '';

  constructor(private authService: AuthService,
    private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if(this.isLoggedIn){
      this.currentUser = this.authService.getCurrentUser();
      this.freeSpace = formatBytes(this.currentUser.freeSpaceAvailable!, 1);
      this.occupiedSpace = formatBytes(this.currentUser.assignedCloudStorage! - this.currentUser.freeSpaceAvailable!, 1);
    }
  }

  onLogout(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
