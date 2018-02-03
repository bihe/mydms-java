import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/timeoutWith';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/observable/throw';

import { ApplicationData } from '../models/application.data';
import { BaseService } from '../services/_base.service';
import { AppInfo } from '../models/app.info';
import { Result } from '../models/result.model';
import { Document } from '../models/document.model';
import { DocumentResult } from '../models/document.result.model';

@Injectable()
export class AppDataService extends BaseService {
  private readonly SEARCH_DOCUMENTS: string = '/api/v1/documents/search';
  private readonly APP_INFO_URL: string = '/api/v1/appinfo';
  private readonly SEARCH_SENDERS_URL: string = '/api/v1/senders/search';
  private readonly SEARCH_TAGS_URL: string = '/api/v1/tags/search';
  private readonly SAVE_DOCUMENTS_URL: string = '/api/v1/documents/';
  private readonly LOAD_DOCUMENT_URL: string = '/api/v1/documents/%ID%';
  private readonly GDRIVE_URL: string = '/api/v1/gdrive';

  constructor (private http: Http) {
    super();
  }

  getApplicationInfo(): Observable<AppInfo> {
    return this.http.get(this.APP_INFO_URL, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<AppInfo>(res);
      })
      .catch(this.handleError);
  }

  searchDocuments(title: string, pageSize: number, skipEntries: number): Observable<DocumentResult> {
    const searchUrl = 'title=%TITLE%&limit=%LIMIT%&skip=%SKIP%';
    if (title && title !== '') {
      title = encodeURIComponent(title);
    }
    let url = this.SEARCH_DOCUMENTS + '?' + searchUrl.replace('%TITLE%', title || '');
    if (!pageSize) {
      url = url.replace('%LIMIT%', '');
    } else {
      url = url.replace('%LIMIT%', pageSize.toString());
    }
    if (!skipEntries) {
      url = url.replace('%SKIP%', '');
    } else {
      url = url.replace('%SKIP%', skipEntries.toString());
    }

    return this.http.get(url, this.getRequestOptions())
      .distinctUntilChanged()   // ignore if next search term is same as previous
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  isGDriveLinked(): Observable<boolean> {
    return this.http.get(this.GDRIVE_URL, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<boolean>(res);
      })
      .catch(this.handleError);
  }

  unlinkGDrive(): Observable<Result> {
    return this.http.delete(this.GDRIVE_URL, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<Result>(res);
      })
      .catch(this.handleError);
  }

  searchSenders(name: string): Observable<any[]> {
    const searchUrl = 'name=%NAME%';
    const url = this.SEARCH_SENDERS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .distinctUntilChanged()   // ignore if next search term is same as previous
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  searchTags(name: string): Observable<any[]> {
    const searchUrl = 'name=%NAME%';
    const url = this.SEARCH_TAGS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .distinctUntilChanged()   // ignore if next search term is same as previous
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  saveDocument(document: Document): Observable<Result> {
    return this.http.post(this.SAVE_DOCUMENTS_URL, JSON.stringify(document), this.getRequestOptions())
              .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
              .map(this.extractData)
              .catch(this.handleError);

  }

  getDocument(id: string): Observable<Document> {
    const url = this.LOAD_DOCUMENT_URL.replace('%ID%', id || '-1');

    return this.http.get(url, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<Document>(res);
      })
      .catch(this.handleError);
  }

  deleteDocument(id: string): Observable<Result> {
    const url = this.LOAD_DOCUMENT_URL.replace('%ID%', id || '-1');

    return this.http.delete(url, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(this.extractData)
      .catch(this.handleError);
  }
}
