import { Component, OnInit } from '@angular/core';

import { BackendService } from '../backend/index';
import { UserInfo } from '../models/userinfo.model';
import { DataModel } from '../models/data.model';

@Component({
  moduleId: module.id,
  selector: 'mydms-navbar',
  templateUrl: 'navbar.component.html',
  styleUrls: ['navbar.component.css']
})
export class NavbarComponent implements OnInit {
  public userInfo: UserInfo = new UserInfo();

  constructor(private backend:BackendService, private data:DataModel) {}

  ngOnInit() {
    this.getUserInfo();
  }

  getUserInfo() {
    if(!this.data.userInfo) {
      this.backend.getUserInfo()
      .subscribe(
        info => {
          this.userInfo = info;
          this.data.userInfo = this.userInfo;
        },
        error => { window.alert(<any>error); }
      );
    } else {
      this.userInfo = this.data.userInfo;
    }
  }
}
