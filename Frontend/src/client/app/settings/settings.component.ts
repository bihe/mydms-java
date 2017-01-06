import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService, BaseComponent } from '../shared/index';

import { Message } from 'primeng/primeng';



/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'mydms-settings',
  templateUrl: 'settings.component.html',
  styleUrls: ['settings.component.css'],
})
export class SettingsComponent extends BaseComponent implements OnInit {

  isLinked:boolean = false;
  backendLinked:boolean = false;

  constructor(private backend:BackendService, private router:Router) {
    super();
  }

  ngOnInit() {
    this.backend.isGDriveLinked().subscribe(
        result => {
          if(result) {
            this.backendLinked = this.isLinked = result;
          }
        },
        error => {
          this.showErrorMessage('Backend-Error', error);
        }
      );
  }

  onCancel() {
    this.router.navigate(['/']);
  }

  onSave() {
    if(this.isLinked === false) {
      // unlink the Google Drive backend
      this.backend.unlinkGDrive().subscribe(
        result => {
          if(result) {
            if(result.result === 'Deleted') {
              console.debug(result.message);
              this.backendLinked = this.isLinked = false;

              this.msgs = [];
              this.msgs.push({severity:'info', summary:'Backend', detail:'Successfully unlinked the Backend!'});

              return;
            } else {
              this.showErrorMessage('Backend-Error', 'Could not unlink the Backend!');
            }
          }
        },
        error => {
          this.showErrorMessage('Backend-Error', error);
        }
      );
    } else {
      window.location.href = '/api/gdrive/link';
      return;
    }
  }

}
