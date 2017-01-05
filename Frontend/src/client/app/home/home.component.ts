import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../shared/index';
import { Document, Tag, Sender, DataModel } from '../shared/index';

import { Message } from 'primeng/primeng';

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

  documents: Array<Document> = new Array<Document>();
  msgs: Message[] = [];

  readonly InitialPageSize:number = 20;
  private pagedDocuments: Array<Document> = null;
  private searchString:string = null;

  constructor(private backend:BackendService, private data:DataModel, private router:Router,) {}

  ngOnInit() {
    this.data.setIsActive(false);
    this.searchDocuments(null, 0);
  }

  /**
   * rather simple infinite scrolling logic
   */
  onScroll(event:any) {
    let scrollingElement:any = event.srcElement.scrollingElement;
    let scrollHeight:number = scrollingElement.scrollHeight;
    let scrollTop:number = scrollingElement.scrollTop;
    let offsetHeight:number = scrollingElement.offsetHeight;

    // calculate the remaining scrolling-space
    // @see http://stackoverflow.com/questions/4244841/how-to-know-the-end-of-scrolling-event-for-a-div-tag
    if(scrollHeight - scrollTop === offsetHeight) {
      let skip:number = this.documents.length;
      console.debug('Load additional entries from backend. skip: ' + skip);
      this.searchDocuments(this.searchString, skip);

      this.msgs = [];
      if(skip > 0) {
        this.msgs.push({severity:'info', summary:'Data Loaded', detail:'Between ' + skip + ' and ' + (skip + this.InitialPageSize)});
      }
    }
  }

  /**
   * the search was triggered by user interaction
   */
  onSearch(searchText:string) {
    this.documents = new Array<Document>();
    this.searchString = searchText;
    this.searchDocuments(searchText, 0);
  }

  onNavigateTo(id:string) {
    console.debug('Got id: ' + id);
    this.router.navigate(['/document', id]);
  }

  searchDocuments(title:string, skipEntries:number) {
    this.data.setIsActive(true);

    this.backend.searchDocuments(title, this.InitialPageSize, skipEntries)
      .subscribe(
        result => {
          this.pagedDocuments = new Array<Document>();
          result.forEach(a => {
            let doc = new Document();
            doc.title = a.title;
            doc.created = a.created;
            doc.modified = a.modified;
            doc.amount = a.amount;
            doc.fileName = a.fileName;
            doc.encodedFilename = btoa(a.fileName);
            doc.id = a.id;

            let tags = a.tags as Array<Tag>;
            doc.tags = tags;

            let senders = a.senders as Array<Sender>;
            doc.senders = senders;

            this.pagedDocuments.push(doc);
          });

          this.documents = this.documents.concat(this.pagedDocuments);

          this.data.setIsActive(false);
        },
        error => {
          this.data.setIsActive(false);
          window.alert(<any>error);
        }
      );
  }

}
