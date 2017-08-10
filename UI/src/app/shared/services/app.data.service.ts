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
import { UserInfo } from '../models/user.info';

@Injectable()
export class AppDataService extends BaseService {
  private readonly SEARCH_DOCUMENTS: string = '/api/documents/search';
  private readonly USER_INFO_URL: string = '/api/userinfo';
  private readonly SEARCH_SENDERS_URL: string = '/api/senders/search';
  private readonly SEARCH_TAGS_URL: string = '/api/tags/search';
  private readonly SAVE_DOCUMENTS_URL: string = '/api/documents/';
  private readonly LOAD_DOCUMENT_URL: string = '/api/documents/%ID%';
  private readonly GDRIVE_LINKED_URL: string = '/api/gdrive/islinked';
  private readonly GDRIVE_UNLINK_URL: string = '/api/gdrive';

  constructor (private http: Http) {
    super();
  }

  getUserInfo(): Observable<UserInfo> {
    return this.http.get(this.USER_INFO_URL, this.getRequestOptions())
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<UserInfo>(res);
      })
      .catch(this.handleError);
  }

  searchDocuments(title: string, pageSize: number, skipEntries: number): Observable<any[]> {
    const searchUrl = 'title=%TITLE%&limit=%LIMIT%&skip=%SKIP%';
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
      .debounceTime(300)        // wait for 300ms pause in events
      .timeoutWith(this.RequestTimeOutDefault, Observable.throw(new Error('Timeout exceeded!')))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }
}
