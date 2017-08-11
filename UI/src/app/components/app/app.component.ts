import { Component } from '@angular/core';
import { ApplicationState } from '../../shared/services/app.state';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {

  public version: string;
  public displayName: string;
  public year: number;

  constructor(private state: ApplicationState) {
    this.state.getAppData().subscribe(x => {
      this.displayName = x.appInfo.userInfo.displayName;
      this.version = x.appInfo.versionInfo.versionString;
    });
    this.year = new Date().getFullYear();
  }
}
