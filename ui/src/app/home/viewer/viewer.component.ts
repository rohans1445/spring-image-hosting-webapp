import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Image } from 'src/app/model/image.model';
import { formatBytes } from 'src/app/util/helpers';

@Component({
  selector: 'app-viewer',
  templateUrl: './viewer.component.html',
  styleUrls: ['./viewer.component.css']
})
export class ViewerComponent implements OnInit {

  constructor() { }

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

}
