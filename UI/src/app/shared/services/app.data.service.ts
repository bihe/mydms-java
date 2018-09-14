import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import { catchError, distinctUntilChanged, map, timeout } from 'rxjs/operators';
import { AppInfo } from '../models/app.info';
import { Document } from '../models/document.model';
import { DocumentResult } from '../models/document.result.model';
import { Result } from '../models/result.model';
import { BaseService } from '../services/_base.service';


@Injectable()
export class AppDataService extends BaseService {
  private readonly SEARCH_DOCUMENTS: string = '/api/v1/documents/search';
  private readonly APP_INFO_URL: string = '/api/v1/appinfo';
  private readonly SEARCH_SENDERS_URL: string = '/api/v1/senders/search';
  private readonly SEARCH_TAGS_URL: string = '/api/v1/tags/search';
  private readonly SAVE_DOCUMENTS_URL: string = '/api/v1/documents/';
  private readonly LOAD_DOCUMENT_URL: string = '/api/v1/documents/%ID%';

  constructor (private http: Http) {
    super();
  }

  getApplicationInfo(): Observable<AppInfo> {
    return this.http.get(this.APP_INFO_URL, this.getRequestOptions())
      .pipe(
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<AppInfo>(res);
        }),
        catchError(this.handleError)
      );
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
      .pipe(
        distinctUntilChanged(),
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<DocumentResult>(res);
        }),
        catchError(this.handleError)
      );
  }

  searchSenders(name: string): Observable<any[]> {
    const searchUrl = 'name=%NAME%';
    const url = this.SEARCH_SENDERS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .pipe(
        distinctUntilChanged(),
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<any[]>(res);
        }),
        catchError(this.handleError)
      );
  }

  searchTags(name: string): Observable<any[]> {
    const searchUrl = 'name=%NAME%';
    const url = this.SEARCH_TAGS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .pipe(
        distinctUntilChanged(),
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<any[]>(res);
        }),
        catchError(this.handleError)
      );
  }

  saveDocument(document: Document): Observable<Result> {
    return this.http.post(this.SAVE_DOCUMENTS_URL, JSON.stringify(document), this.getRequestOptions())
      .pipe(
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<Result>(res);
        }),
        catchError(this.handleError)
      );

  }

  getDocument(id: string): Observable<Document> {
    const url = this.LOAD_DOCUMENT_URL.replace('%ID%', id || '-1');

    return this.http.get(url, this.getRequestOptions())
      .pipe(
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<Document>(res);
        }),
        catchError(this.handleError)
      );
  }

  deleteDocument(id: string): Observable<Result> {
    const url = this.LOAD_DOCUMENT_URL.replace('%ID%', id || '-1');

    return this.http.delete(url, this.getRequestOptions())
      .pipe(
        timeout(this.RequestTimeOutDefault),
        map(res => {
          return this.extractData<Result>(res);
        }),
        catchError(this.handleError)
      );
  }
}
