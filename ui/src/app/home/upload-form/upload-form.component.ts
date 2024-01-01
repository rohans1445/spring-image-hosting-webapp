import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ImageService } from 'src/app/services/image.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-upload-form',
  templateUrl: './upload-form.component.html',
  styleUrls: ['./upload-form.component.css']
})
export class UploadFormComponent implements OnInit {

  uploadedFile?: File;
  visibility: string = 'PRIVATE';
  message = {text: '', color: ''};

  constructor(private imageService: ImageService,
    private userService: UserService) { }

  ngOnInit(): void {
  }

  onFileSelected(event: any){
    this.uploadedFile = event.target.files[0];
  }

  onSubmit(){

    if(this.uploadedFile){
      this.imageService.uploadImage({file: this.uploadedFile, visibility: this.visibility}).subscribe({
        next: res => {
          this.message.text = "Uploaded " + this.uploadedFile!.name;
          this.message.color = 'white';
          this.userService.storageUpdated.next(true);
        },
        error: (err: HttpErrorResponse) => {
          this.message.color = 'red'
          if(err.status === 500){
            this.message.text = 'Could not upload image - Internal Server Error';
          } else if (err.status === 400){
            this.message.text = 'You have do not have any free storage left';
          }
        }
      });
    } else {
      this.message.text = 'Please select a file'
    }

  }

}
