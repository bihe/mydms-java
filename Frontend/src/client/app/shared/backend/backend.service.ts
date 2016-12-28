import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
// import 'rxjs/add/operator/do';  // for debugging

import { BaseService } from './base.service';
import { UserInfo } from '../models/userinfo.contract';

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

  searchDocuments(): Observable<any[]> {
    return this.http.get(this.SEARCH_DOCUMENTS, this.getRequestOptions())
      //.timeout(this.RequestTimeOutDefault, new Error('Timeout exceeded!'))
      .map(res => {
        return this.extractData<any[]>(res);
      })
      .catch(this.handleError);
  }
}
