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
  public build: string;

  constructor(private state: ApplicationState) {
    this.state.getAppData().subscribe(x => {
      this.displayName = x.appInfo.userInfo.displayName;
      this.version = x.appInfo.versionInfo.version;
      this.build = x.appInfo.versionInfo.buildNumber;
    });
    this.year = new Date().getFullYear();
  }
}
