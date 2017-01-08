import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';

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

  menuVisible:boolean = false;

  constructor(private backend:BackendService,
    private data:DataModel,
    private sanitizer: DomSanitizer,
    private router:Router) {}

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

  navigateTo(destination:string) {
    this.toggleMenu(false);
    this.router.navigate([destination]);
  }

  toggleMenu(visible:boolean) {
    this.menuVisible = visible;
  }

  menuTransform() {
    if(this.menuVisible) {
      return this.sanitizer.bypassSecurityTrustStyle('translateX(0)');
    } else {
      return this.sanitizer.bypassSecurityTrustStyle('translateX(-110%)');
    }
  }

}
