import { Component, OnInit } from '@angular/core';
import { BackendService } from '../shared/index';

/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'sd-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
})
export class HomeComponent implements OnInit {

  documents: any[] = [];

  constructor(private backend:BackendService) {}

  ngOnInit() {
    this.searchDocuments();
  }

  searchDocuments() {
    this.backend.searchDocuments()
      .subscribe(
        result => {
          this.documents = result;
        },
        error => { window.alert(<any>error); }
      );
  }

}
