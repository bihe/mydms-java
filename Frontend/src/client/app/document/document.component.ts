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
  uploadFileName:string = '';
  senders:Sender[];
  senderResults:Sender[] = [];

  private uploadToken:string = '';

  constructor(
    private backend:BackendService,
    private router:Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.clearError();

    let id = +this.route.snapshot.params['id'] || -1;
    console.debug('Got route id: ' + id);


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

          console.debug(result);

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
            s.create = true;
            s.name = event.query;
            this.senderResults.push(s);
          }
        },
        error => {
          window.alert(<any>error);
        }
      );
  }

  private clearError() {
    this.error = this.errorTitle = '';
  }

  private showError(title:string, message:string) {
    this.errorTitle = title;
    this.error = message;
  }

}
