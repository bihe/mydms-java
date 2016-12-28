import { Headers, RequestOptions, Response } from '@angular/http';
import { Observable }     from 'rxjs/Observable';

export class BaseService {

  private readonly JSON:string = 'application/json';

  protected get RequestTimeOutDefault(): number { return 1000 * 60 * 1; }
  protected get RequestTimeOutLongRunning(): number { return 1000 * 60 * 10; }

  protected getRequestHeaders() {
    let headers = new Headers({
      'Content-Type': this.JSON,
      'Accept': this.JSON
    });
    return { headers: headers };
  }

  protected getRequestOptions():RequestOptions {
    let options = new RequestOptions(this.getRequestHeaders());
    return options;
  }

  protected handleError (error: any) {
    let errMsg = '';
    try {
      let errorObject = error.json();
      errMsg = errorObject.Error + ': ' + errorObject.Description || 'Server error';
    } catch(ex) {
      errMsg = (error.message) ? error.message : 'Server error: ' + error;
    }
    console.error(error);
    return Observable.throw(errMsg);
  }

  protected extractRaw(res: Response) {
    if (res.status < 200 || res.status >= 300) {
      throw new Error('Bad response status: ' + res.status);
    }
    let body = res.text().length > 0 ? res.text() : '';
    return body;
  }

  protected extractData<T>(res: Response):T {
    if (res.status < 200 || res.status >= 300) {
      throw new Error('Bad response status: ' + res.status);
    }
    let data:T = res.text().length > 0 ? <T>res.json() as T : null;
    return data;
  }
}
