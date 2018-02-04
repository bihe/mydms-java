import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';
import { ApplicationState } from '../../shared/services/app.state';
import { ApplicationData } from '../../shared/models/application.data';
import { MatSnackBar } from '@angular/material';
import { MessageUtils } from '../../shared/utils/message.utils';
import { AppDataService } from '../../shared/services/app.data.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  public A: ApplicationData;
  public isLinked = false;
  public backendLinked = false;


  constructor(
    private service: AppDataService,
    private state: ApplicationState,
    private snackBar: MatSnackBar,
    private sanitizer: DomSanitizer,
    private router: Router) {
  }

  ngOnInit() {
    this.service.getApplicationInfo()
      .subscribe(
        data => {
          this.A = new ApplicationData();
          this.A.appInfo = data;
          this.state.setAppData(this.A);
        },
        error => {
          new MessageUtils().showError(this.snackBar, error);
        }
      );

    this.service.isGDriveLinked().subscribe(
        result => {
          if (result) {
            this.backendLinked = this.isLinked = result;
          }
        },
        error => {
          new MessageUtils().showError(this.snackBar, error);
        }
      );
  }

  onCancel() {
    this.router.navigate(['/home']);
  }

  onSave() {
    if (this.isLinked === false) {
      // unlink the Google Drive backend
      this.service.unlinkGDrive().subscribe(
        result => {
          if (result) {
            if (result.result === 'Deleted') {
              console.log(result.message);
              this.backendLinked = this.isLinked = false;
              new MessageUtils().showSuccess(this.snackBar, 'Successfully unlinked the Backend!');
              return;
            } else {
              new MessageUtils().showError(this.snackBar, 'Could not unlink the Backend!');
            }
          }
        },
        error => {
          new MessageUtils().showError(this.snackBar, error);
        }
      );
    } else {
      window.location.href = '/api/gdrive/link';
      return;
    }
  }
}
