import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
// import 'rxjs/add/operator/do';  // for debugging
import 'rxjs/Rx';

import { BaseService } from './base.service';
import { UserInfo } from '../models/userinfo.model';
import { Document } from '../models/document.model';
import { Result } from '../models/result.model';

@Injectable()
export class BackendService extends BaseService {

  private readonly SEARCH_DOCUMENTS:string = '/api/documents/search';
  private readonly USER_INFO_URL:string = '/api/userinfo';
  private readonly SEARCH_SENDERS_URL:string = '/api/senders/search';
  private readonly SEARCH_TAGS_URL:string = '/api/tags/search';
  private readonly SAVE_DOCUMENTS_URL:string = '/api/documents/';

  constructor(private http: Http) {
    super();
  }

  getUserInfo(): Observable<UserInfo> {
    return this.http.get(this.USER_INFO_URL, this.getRequestOptions())
      .timeout(this.RequestTimeOutDefault)
      .map(res => {
        return this.extractData<UserInfo>(res);
      })
      .catch(this.handleError);
  }

  searchDocuments(title:string, pageSize:number, skipEntries:number): Observable<any[]> {
    let searchUrl = 'title=%TITLE%&limit=%LIMIT%&skip=%SKIP%';
    let url = this.SEARCH_DOCUMENTS + '?' + searchUrl.replace('%TITLE%', title || '');
    if(!pageSize) {
      url = url.replace('%LIMIT%', '');
    } else {
      url = url.replace('%LIMIT%', pageSize.toString());
    }
    if(!skipEntries) {
      url = url.replace('%SKIP%', '');
    } else {
      url = url.replace('%SKIP%', skipEntries.toString());
    }

    console.debug('Url: ' + url);

    return this.http.get(url, this.getRequestOptions())
      .timeout(this.RequestTimeOutDefault)
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  searchSenders(name:string): Observable<any[]> {
    let searchUrl = 'name=%NAME%';
    let url = this.SEARCH_SENDERS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .debounceTime(300)        // wait for 300ms pause in events
      .distinctUntilChanged()   // ignore if next search term is same as previous
      .timeout(this.RequestTimeOutDefault)
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  searchTags(name:string): Observable<any[]> {
    let searchUrl = 'name=%NAME%';
    let url = this.SEARCH_TAGS_URL + '?' + searchUrl.replace('%NAME%', name || '');

    return this.http.get(url, this.getRequestOptions())
      .debounceTime(300)        // wait for 300ms pause in events
      .distinctUntilChanged()   // ignore if next search term is same as previous
      .timeout(this.RequestTimeOutDefault)
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }

  saveDocument(document:Document) : Observable<Result> {
    return this.http.post(this.SAVE_DOCUMENTS_URL, JSON.stringify(document), this.getRequestOptions())
              .timeout(this.RequestTimeOutDefault)
              .map(this.extractData)
              .catch(this.handleError);

  }
}
