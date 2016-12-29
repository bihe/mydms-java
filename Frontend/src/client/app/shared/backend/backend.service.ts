import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
// import 'rxjs/add/operator/do';  // for debugging

import { BaseService } from './base.service';
import { UserInfo } from '../models/userinfo.model';

@Injectable()
export class BackendService extends BaseService {

  private readonly SEARCH_DOCUMENTS:string = '/api/documents/search';
  private readonly USER_INFO_URL:string = '/api/userinfo';

  constructor(private http: Http) {
    super();
  }

  getUserInfo(): Observable<UserInfo> {
    return this.http.get(this.USER_INFO_URL, this.getRequestOptions())
      //.timeout(this.RequestTimeOutDefault, new Error('Timeout exceeded!'))
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
      //.timeout(this.RequestTimeOutDefault, new Error('Timeout exceeded!'))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }
}
