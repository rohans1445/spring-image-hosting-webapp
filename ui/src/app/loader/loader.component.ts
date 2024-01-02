import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loader',
  template: `
  <div class="pt-5 text-center">
    <span class="loader"></span>
  </div>`,
  styleUrls: ['./loader.component.css']
})
export class LoaderComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
