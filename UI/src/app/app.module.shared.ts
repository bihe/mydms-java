import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule, MdNativeDateModule } from '@angular/material';

import { AppComponent } from './components/app/app.component';
import { EllipsisPipe } from './shared/pipes/ellipsis';
import { DateFormatPipe } from './shared/pipes/date.format';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';

import { ApplicationState } from './shared/services/app.state';
import { AppDataService } from './shared/services/app.data.service';

// import { HomeComponent } from './components/home/home.component';
// import { UserEditDialogComponent } from "./components/home/home.useredit.component";
// import { DateFormatPipe } from "app/shared/pipes/date.format";
// import { DateValidator } from "app/shared/validators/date.validator";
// import { MyDatePickerModule } from 'mydatepicker';
// import { UserService } from "app/shared/services/user.service";
// import { AppDataService } from "app/shared/services/appdata.service";
// import { AuthComponent } from "app/components/auth/auth.component";
// import { MasterDataComponent } from "app/components/masterdata/masterdata.component";
// import { EllipsisPipe } from "app/shared/pipes/ellipsis";


@NgModule({
  imports: [ MaterialModule ],
  exports: [ MaterialModule ],
})
export class AppMaterialModule { }



export const sharedConfig: NgModule = {
    bootstrap: [ AppComponent ],
    declarations: [
        AppComponent,
        NavbarComponent,
        HomeComponent,
        DateFormatPipe,
        EllipsisPipe
    ],
    imports: [
        AppMaterialModule
    ],
    providers: [ ApplicationState, AppDataService ],
    entryComponents: []
};
