import { Component, OnInit, EventEmitter  } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApplicationState } from '../../shared/services/app.state';
import { ApplicationData } from '../../shared/models/application.data';
import { MdSnackBar, MdDialog, MdDialogConfig } from '@angular/material';
import { MessageUtils } from '../../shared/utils/message.utils';
import { AppDataService } from '../../shared/services/app.data.service';
import { Document } from '../../shared/models/document.model';
import { Sender } from '../../shared/models/sender.model';
import { Tag } from '../../shared/models/tag.model';
import { ConfirmationDialogComponent } from '../../shared/confirmation/confirmation.component';
import { AutoCompleteModel, TagType } from '../../shared/models/autocomplete.model';
import { UploadOutput, UploadInput, UploadFile, humanizeBytes } from 'ngx-uploader';
import { TagModel } from 'ngx-chips/dist/modules/core';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/first';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/of';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css']
})
export class DocumentComponent implements OnInit {

  isNewDocument = true;
  document: Document = new Document();
  senders: Sender[] = [];
  tags: Tag[] = [];

  selectedTags: any[] = [];
  selectedSenders: any[] = [];

  files: UploadFile[];
  uploadInput: EventEmitter<UploadInput>;
  humanizeBytes: Function;
  dragOver: boolean;

  documentTitle = '';
  documentAmount = 0;
  uploadFileName = '';
  private uploadToken = '';
  encodedUploadFileName = '';

  public A: ApplicationData;

  constructor(
    private service: AppDataService,
    private state: ApplicationState,
    private snackBar: MdSnackBar,
    private route: ActivatedRoute,
    private dialog: MdDialog,
    private router: Router) {
      this.files = []; // local uploading files array
      this.uploadInput = new EventEmitter<UploadInput>(); // input events, we use this to emit data to ngx-uploader
      this.humanizeBytes = humanizeBytes;
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

    const id = this.route.snapshot.params['id'] || -1;
    console.log('Got route id: ' + id);
    if (id === -1 || id === '-1') {
      this.document = null;
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

            this.documentTitle = this.document.title;
            this.documentAmount = this.document.amount;
            this.uploadFileName = this.document.fileName;
            this.uploadToken = '-';
            this.encodedUploadFileName = this.document.previewLink;

            if (this.document.senders) {
              this.document.senders.forEach(item => {
                const sender: any = {};
                sender.value = item;
                sender.display = item;
                this.selectedSenders.push(sender);
              });
            }

            if (this.document.tags) {
              this.document.tags.forEach(item => {
                const tag: any = {};
                tag.value = item;
                tag.display = item;
                this.selectedTags.push(tag);
              });
            }

            this.state.setProgress(false);
          }
        },
        error => {
          this.state.setProgress(false);
          new MessageUtils().showError(this.snackBar, error);
        }
      );


  }

  public onCancel() {
    this.router.navigate(['/home']);
  }

  public onSave() {
    if (this.isFormValid()) {
      this.convertSenderAndTags();

      if (this.document === null) {
        this.document = new Document();
      }

      this.document.title = this.documentTitle;
      this.document.amount = this.documentAmount;
      this.document.senders = this.senders.map(a => a.name);
      this.document.tags = this.tags.map(a => a.name);
      this.document.uploadFileToken = this.uploadToken;
      this.document.fileName = this.uploadFileName;

      this.state.setProgress(true);
      this.service.saveDocument(this.document)
        .subscribe(
          result => {
            if (result) {
              if (result.result === 'Created') {
                this.state.setProgress(false);
                console.log(result.message);
                this.router.navigate(['/']);
                return;
              } else {
                this.state.setProgress(false);
                new MessageUtils().showError(this.snackBar, result.message);
              }
            }
          },
          error => {
            this.state.setProgress(false);
            new MessageUtils().showError(this.snackBar, error);
          }
        );
    } else {
      new MessageUtils().showError(this.snackBar, 'The form is not valid!');
    }
  }

  public onDelete() {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, ConfirmationDialogComponent.getDialogConfig(this.documentTitle));

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        console.log('Delete confirmed!');
        this.state.setProgress(true);
        this.service.deleteDocument(this.document.id)
          .subscribe(
            r => {
              if (r.result === 'Deleted') {
                this.state.setProgress(false);
                console.log(r.message);
                this.router.navigate(['/']);
                return;
              } else {
                this.state.setProgress(false);
                new MessageUtils().showError(this.snackBar, result.message);
              }
            },
            error => {
              this.state.setProgress(false);
              new MessageUtils().showError(this.snackBar, error);
            }
          );

      }
    });
  }

  public onUploadOutput(output: UploadOutput): void {
    if (output.type === 'allAddedToQueue') { // when all files added in queue
      this.state.setProgress(true);
      const event: UploadInput = {
        type: 'uploadAll',
        url: '/api/upload/file',
        method: 'POST',
        // data: { foo: 'bar' },
        concurrency: 0
      };
      this.uploadInput.emit(event);
    } else if (output.type === 'addedToQueue'  && typeof output.file !== 'undefined') { // add file to array when added
      this.files.push(output.file);
    } else if (output.type === 'uploading' && typeof output.file !== 'undefined') {
      // update current data in files array for uploading file
      const index = this.files.findIndex(file => typeof output.file !== 'undefined' && file.id === output.file.id);
      this.files[index] = output.file;
    } else if (output.type === 'removed') {
      // remove file from array when removed
      this.files = this.files.filter((file: UploadFile) => file !== output.file);
    } else if (output.type === 'dragOver') {
      this.dragOver = true;
    } else if (output.type === 'dragOut') {
      this.dragOver = false;
    } else if (output.type === 'drop') {
      this.dragOver = false;
    } else if (output.type === 'cancelled') {
      this.files = [];
    } else if (output.type === 'done') {
      this.state.setProgress(false);
      const response: any = output.file.response;
      if (output.file.responseStatus === 200 && response.result === 'Created') {
        // done!
        this.files = [];
        this.uploadToken = response.token;
        this.uploadFileName = output.file.name;


        new MessageUtils().showSuccess(this.snackBar, response.message);
      } else {
        console.log(output.file.response);
        new MessageUtils().showError(this.snackBar, response.message);
      }

    }
  }

  public searchForTags = (text: string): Observable<any[]> => {
    return this.service.searchTags(text).map(a => {
      // change the type of the array to meet the 'expectations' of ngx-chips
      return this.mapAutocomplete(a, TagType.Tag);
    });
  }

  public searchForSenders = (text: string): Observable<any[]> => {
    return this.service.searchSenders(text).map(a => {
      // change the type of the array to meet the 'expectations' of ngx-chips
      return this.mapAutocomplete(a, TagType.Sender);
    });
  }

  public onClearUploadedFile() {
    this.files = [];
    this.uploadToken = '';
    this.uploadFileName = '';
  }

  public isFormValid() {
    if (this.documentTitle !== '' && this.uploadFileName !== '' && this.uploadToken !== ''
      && this.selectedSenders.length > 0) {
        return true;
      }
    return false;
  }

  private mapAutocomplete(items: any[], type: TagType): AutoCompleteModel[] {
    const autocompletion: AutoCompleteModel[] = [];
    items.forEach(x => {
      const item = new AutoCompleteModel();
      item.display = x.name;
      item.value = x.id;
      item.type = type;
      autocompletion.push(item);
    });
    // return autocompletion;
    return autocompletion;
  }

  private convertSenderAndTags() {
    if (this.selectedSenders) {
      this.senders = [];
      this.selectedSenders.forEach(item => {
        const sender = new Sender();
        sender.name = item.display;
        sender.id = +item.value ? item.value : -1;
        this.senders.push(sender);
      });
    }

    if (this.selectedTags) {
      this.tags = [];
      this.selectedTags.forEach(item => {
        const tag = new Tag();
        tag.name = item.display;
        tag.id = +item.value ? item.value : -1;
        this.tags.push(tag);
      });
    }
  }
}
