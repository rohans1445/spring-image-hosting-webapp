import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Image } from 'src/app/model/image.model';
import { AuthService } from 'src/app/services/auth.service';
import { ImageService } from 'src/app/services/image.service';
import { UserService } from 'src/app/services/user.service';
import { formatBytes } from 'src/app/util/helpers';

@Component({
  selector: 'app-viewer',
  templateUrl: './viewer.component.html',
  styleUrls: ['./viewer.component.css']
})
export class ViewerComponent implements OnInit {

  constructor(private userService: UserService,
    private auth: AuthService,
    private imageService: ImageService) { }

  fileSize: string = '';
  
  @Input()
  image!: Image;

  @Output()
  modalClose = new EventEmitter<void>();

  ngOnInit(): void {
    this.fileSize = formatBytes(this.image.size!, 1);
  }

  
  onClickBackdrop(){
    this.modalClose.emit();
  }

  onClickDelete(){
    this.userService.deleteImage(this.image.id!).subscribe({
      next: res => {
        this.onClickBackdrop();
        this.userService.storageUpdated.next(true);
        this.imageService.imageChange.next(true);
      }
    });
  }

  onClickVisibility(visibility: string){
    if(this.image.visibility === visibility) return;

    this.userService.updateImage(this.image.id!, visibility).subscribe({
      next: res => {
        this.image.visibility = visibility;
        this.imageService.imageChange.next(true);
      }
    });    

  }

}
