import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MdDialog, MdDialogConfig, MdSnackBar } from '@angular/material';
import { ApplicationState } from '../../shared/services/app.state';
import { MessageUtils } from '../../shared/utils/message.utils';
import { AppDataService } from '../../shared/services/app.data.service';
import { Document } from '../../shared/models/document.model';

import * as moment from 'moment';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  documents: Array<Document> = new Array<Document>();

  readonly InitialPageSize: number = 20;
  private pagedDocuments: Array<Document> = null;
  private searchString: string = null;

  constructor(
    private state: ApplicationState,
    private service: AppDataService,
    private router: Router,
    private snackBar: MdSnackBar) {
  }

  ngOnInit() {
    this.searchDocuments(null, 0);
  }

  searchDocuments(title: string, skipEntries: number) {
    // this.data.setIsActive(true);

    this.service.searchDocuments(title, this.InitialPageSize, skipEntries)
      .subscribe(
        result => {
          this.pagedDocuments = new Array<Document>();
          result.forEach(a => {
            const doc = new Document();
            doc.title = a.title;
            doc.created = a.created;
            doc.modified = a.modified;
            doc.amount = a.amount;
            doc.fileName = a.fileName;
            doc.encodedFilename = btoa(encodeURI(a.fileName));
            doc.id = a.id;
            doc.tags = a.tags;
            doc.senders = a.senders;
            doc.dateHuman = moment(doc.lastDate).fromNow();

            this.pagedDocuments.push(doc);
          });

          this.documents = this.documents.concat(this.pagedDocuments);
          this.documents = arrayUnique(this.documents);

          // this.data.setIsActive(false);
        },
        error => {
          // this.data.setIsActive(false);
          new MessageUtils().showError(this.snackBar, error);
        }
      );
  }
}

function arrayUnique(array: any) {
    const a = array.concat();
    for (let i = 0; i < a.length; ++i) {
        for (let j = i + 1; j < a.length; ++j) {
            if (a[i] === a[j]) {
              a.splice(j--, 1);
            }
        }
    }
    return a;
}
