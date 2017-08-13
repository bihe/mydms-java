import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApplicationState } from '../../shared/services/app.state';
import { ApplicationData } from '../../shared/models/application.data';
import { MdSnackBar } from '@angular/material';
import { MessageUtils } from '../../shared/utils/message.utils';
import { AppDataService } from '../../shared/services/app.data.service';
import { Document } from '../../shared/models/document.model';
import { Sender } from '../../shared/models/sender.model';
import { Tag } from '../../shared/models/tag.model';

import 'rxjs/add/operator/mergeMap';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css']
})
export class DocumentComponent implements OnInit {

  isNewDocument = true;
  document: Document = new Document();
  senders: Sender[];
  tags: Tag[];

  public A: ApplicationData;

  constructor(
    private service: AppDataService,
    private state: ApplicationState,
    private snackBar: MdSnackBar,
    private route: ActivatedRoute,
    private router: Router) {
  }

  ngOnInit() {
    this.service.getApplicationInfo()
      .subscribe(
        data => {
          this.A = new ApplicationData();
          this.A.appInfo = data;
          this.state.setAppData(this.A);
        },
        error => {
          new MessageUtils().showError(this.snackBar, error);
        }
      );

    this.isNewDocument = true;

    const id = this.route.snapshot.params['id'] || '';
    console.log('Got route id: ' + id);
    if (id === '-1') {
      return;
    }

    this.state.setProgress(true);
    this.service.getDocument(id)
      .subscribe(
        result => {
          if (result) {

            this.document = result;

            if (this.document.senders && this.document.senders.length > 0) {
              this.senders = this.document.senders.map(a => {
                const s = new Sender();
                s.name = a;
                return s;
              });
            }

            if (this.document.tags && this.document.tags.length > 0) {
              this.tags = this.document.tags.map(a => {
                const t = new Tag();
                t.name = a;
                return t;
              });
            }
            this.isNewDocument = false;
            this.state.setProgress(false);
          }
        },
        error => {
          this.state.setProgress(false);
          new MessageUtils().showError(this.snackBar, error);
        }
      );


  }

  onCancel() {
    this.router.navigate(['/home']);
  }

  onSave() {
  }
}
