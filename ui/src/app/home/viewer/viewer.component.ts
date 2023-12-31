import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Image } from 'src/app/model/image.model';

@Component({
  selector: 'app-viewer',
  templateUrl: './viewer.component.html',
  styleUrls: ['./viewer.component.css']
})
export class ViewerComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  @Input()
  image!: Image;

  @Output()
  modalClose = new EventEmitter<void>();

  onClickBackdrop(){
    this.modalClose.emit();
  }

}
