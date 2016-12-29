import { UserInfo } from './userinfo.model';

export class DataModel {
  public userInfo: UserInfo;
  private active: boolean = false;


  public getIsActive(): boolean {
    return this.active;
  }

  public setIsActive(active:boolean) {
    this.active = active;
  }

}
