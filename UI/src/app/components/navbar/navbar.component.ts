import { Component, OnInit } from '@angular/core';
import { ApplicationState } from '../../shared/services/app.state';
import { ApplicationData } from '../../shared/models/application.data';
import { MdSnackBar } from '@angular/material';
import { MessageUtils } from '../../shared/utils/message.utils';
import { AppDataService } from '../../shared/services/app.data.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  public appData: ApplicationData;

  constructor(
    private service: AppDataService,
    private state: ApplicationState,
    private snackBar: MdSnackBar) {
  }

  ngOnInit() {
    this.service.getUserInfo()
      .subscribe(
        data => {
          this.appData = new ApplicationData();
          this.appData.userInfo = data;
          this.state.setAppData(this.appData);
        },
        error => {
          new MessageUtils().showError(this.snackBar, error);
        }
      );

    // this.userServcie.getAllIterations()
    //   .flatMap(x => {
    //     if (x != null) {
    //       x.forEach(item => {
    //         this.iterations.push({ value: item.value.toString(), viewValue: item.text.toString() });
    //       });
    //       if(x.length > 0) {
    //         this.iteration = x[0].value.toString();
    //         this.iterationText = x[0].text.toString();
    //       }
    //     }
    //     return this.appDataService.getData();
    //   })
    //   .subscribe(
    //     data => {
    //       this.appData = data;
    //       this.appData.iterationText = this.iterationText;
    //       this.appData.iteration = this.iteration;
    //       // share data for later use
    //       this.data.setAppData(this.appData);
    //     },
    //     error => {
    //       new MessageUtils().showError(this.snackBar, error);
    //     }
    //   );
  }
}
