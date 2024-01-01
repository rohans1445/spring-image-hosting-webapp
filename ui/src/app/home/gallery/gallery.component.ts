import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Image } from 'src/app/model/image.model';
import { ImageService } from 'src/app/services/image.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.component.html',
  styleUrls: ['./gallery.component.css']
})
export class GalleryComponent implements OnInit {

  galleryItems: Image[] = [];
  isViewing: boolean = false;
  viewingImage!: Image;
  currentPath: string = '';

  constructor(private imageService: ImageService,
    private route: ActivatedRoute,
    private userService: UserService) { }

  ngOnInit(): void {

    this.loadImages();
      
    this.imageService.imageChange.subscribe({
      next: res => {
        console.log('storage updated - gallery');
        this.loadImages();
      }
    })

  }

  loadImages(){
    this.route.url.subscribe({
      next: res => {
        this.currentPath = res[0].path;

        if(this.currentPath === 'home'){
          this.imageService.getAllImages().subscribe({
            next: (res) => {
              this.galleryItems = res;
            }
          })
        } else if(this.currentPath == 'my-images') {
          this.userService.getUserImages().subscribe({
            next: res => {
              this.galleryItems = res;
            }
          })
        }

      }
    });
  }

  onImageClickInGallery(img: Image){
    this.isViewing = true;
    this.viewingImage = img;
  }

  onModalClose(){
    this.isViewing = false;
  }

}
