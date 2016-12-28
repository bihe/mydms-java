import { Component, OnInit } from '@angular/core';
import { BackendService } from '../shared/index';
import { Document, Tag, Sender } from '../shared/index';

/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'mydms-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
})
export class HomeComponent implements OnInit {

  documents: Array<Document> = null;

  constructor(private backend:BackendService) {}

  ngOnInit() {
    this.searchDocuments();
  }

  searchDocuments() {
    this.backend.searchDocuments()
      .subscribe(
        result => {
          this.documents = new Array<Document>();
          result.forEach(a => {
            let doc = new Document();
            doc.title = a.title;
            doc.created = a.created;
            doc.modified = a.modified;
            doc.amount = a.amount;

            let tags = a.tags as Array<Tag>;
            doc.tags = tags;

            let senders = a.senders as Array<Sender>;
            doc.senders = senders;

            this.documents.push(doc);
          });
        },
        error => { window.alert(<any>error); }
      );
  }

}
