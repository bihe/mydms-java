import { Component, OnInit, HostListener } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { BackendService } from '../shared/index';
import { Document, Tag, Sender, DataModel } from '../shared/index';

@Component({
  moduleId: module.id,
  selector: 'mydms-document',
  templateUrl: 'document.component.html',
  styleUrls: ['document.component.css'],
})
export class DocumentComponent implements OnInit {

  error:string = '';
  errorTitle:string = '';

  senderResults:Sender[] = [];
  tagResults:Tag[] = [];

  isNewDocument:boolean = true;
  document:Document = null;
  documentTitle:string = '';
  documentAmount:number;
  uploadFileName:string = '';
  encodedUploadFileName:string = '';
  senders:Sender[] = [];
  tags:Tag[] = [];
  private uploadToken:string = '';

  constructor(
    private backend:BackendService,
    private router:Router,
    private route: ActivatedRoute,
    private data:DataModel
  ) {}

  ngOnInit() {
    this.clearError();
    this.isNewDocument = true;

    let id = this.route.snapshot.params['id'] || '';
    console.debug('Got route id: ' + id);
    if(id === '-1') {
      return;
    }

    this.data.setIsActive(true);
    this.backend.getDocument(id)
      .subscribe(
        result => {
          if(result) {
            this.document = result;
            this.documentTitle = this.document.title;
            this.documentAmount = this.document.amount;
            this.uploadFileName = this.document.fileName;
            this.encodedUploadFileName = btoa(this.uploadFileName);
            this.uploadToken = '-';

            if(this.document.senders && this.document.senders.length > 0) {
              this.senders = this.document.senders.map(a => {
                let s = new Sender();
                s.name = a;
                return s;
              });
            }

            if(this.document.tags && this.document.tags.length > 0) {
              this.tags = this.document.tags.map(a => {
                let t = new Tag();
                t.name = a;
                return t;
              });
            }

            this.isNewDocument = false;
            this.data.setIsActive(false);
          }
        },
        error => {
          this.data.setIsActive(false);
          this.showError('Load-Error', error);
        }
      );
  }

  onClearUploadedFile() {
    this.uploadToken = '';
    this.uploadFileName = '';
  }

  onCancel() {
    this.router.navigate(['/']);
  }

  onUploadError(event:any) {
    let response = JSON.parse(event.xhr.response);
    console.debug(response.code + ' / ' + response.message);
    this.showError('Upload-Error', response.message);
  }

  onUploadDone(event:any) {
    this.clearError();
    console.debug(event);
    let response = JSON.parse(event.xhr.response);
    if(response.result === 'Created') {
      this.uploadToken = response.token;
      this.uploadFileName = event.files[0].name;
      console.debug('Got token: ' + this.uploadToken);
      console.debug('File: ' + this.uploadFileName);
    } else {
      this.showError('Server-Upload','The upload file could not be stored - please try again');
    }
  }

  onSearchSenders(event:any) {
    this.senderResults = [];

    this.backend.searchSenders(event.query)
      .subscribe(
        result => {
          if(result && result.length > 0) {
            result.forEach(a => {
              let s = new Sender();
              s.id = a.id;
              s.name = a.name;
              this.senderResults.push(s);
            });
          } else {
            let s = new Sender();
            s.id = -1;
            s.name = event.query;

            let index = this.senderResults.findIndex(a => {
              return a.id === s.id && a.name === s.name;
            });
            if(index === -1) {
              this.senderResults.push(s);
            }
          }
        },
        error => {
          window.alert(<any>error);
        }
      );
  }

  onSearchTags(event:any) {
    this.tagResults = [];

    this.backend.searchTags(event.query)
      .subscribe(
        result => {
          if(result && result.length > 0) {
            result.forEach(a => {
              let t = new Tag();
              t.id = a.id;
              t.name = a.name;
              this.tagResults.push(t);
            });
          } else {
            let t = new Tag();
            t.id = -1;
            t.name = event.query;

            let index = this.tagResults.findIndex(a => {
              return a.id === t.id && a.name === t.name;
            });
            if(index === -1) {
              this.tagResults.push(t);
            }
          }
        },
        error => {
          window.alert(<any>error);
        }
      );
  }

  isFormValid() {
    if(this.documentTitle !== '' && this.uploadFileName !== '' && this.uploadToken !== ''
      && this.senders.length > 0) {
        return true;
      }
    return false;
  }

  onSave() {
    if(this.isFormValid()) {

      if(this.document === null) {
        this.document = new Document();
      }

      this.document.title = this.documentTitle;
      this.document.amount = this.documentAmount;
      this.document.senders = this.senders.map(a => a.name);
      this.document.tags = this.tags.map(a => a.name);
      this.document.uploadFileToken = this.uploadToken;
      this.document.fileName = this.uploadFileName;

      this.data.setIsActive(true);
      this.backend.saveDocument(this.document)
        .subscribe(
          result => {
            if(result) {
              if(result.result === 'Created') {
                this.data.setIsActive(false);
                console.debug(result.message);
                this.router.navigate(['/']);
                return;
              } else {
                this.data.setIsActive(false);
                this.showError('Save-Error', result.message);
              }
            }
          },
          error => {
            this.data.setIsActive(false);
            this.showError('Save-Error', error);
          }
        );

    } else {
      this.showError('Form-Error', 'The form input is not valid!');
    }
  }

  private clearError() {
    this.error = this.errorTitle = '';
  }

  private showError(title:string, message:string) {
    this.errorTitle = title;
    this.error = message;
  }

}
