import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule, MdNativeDateModule } from '@angular/material';
import { TagInputModule } from 'ngx-chips';
import { NgUploaderModule } from 'ngx-uploader';

import { AppComponent } from './components/app/app.component';
import { EllipsisPipe } from './shared/pipes/ellipsis';
import { DateFormatPipe } from './shared/pipes/date.format';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';

import { ApplicationState } from './shared/services/app.state';
import { AppDataService } from './shared/services/app.data.service';
import { SettingsComponent } from './components/settings/settings.component';
import { DocumentComponent } from './components/document/document.component';
import { ConfirmationDialogComponent } from './shared/confirmation/confirmation.component';

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
        SettingsComponent,
        ConfirmationDialogComponent,
        DocumentComponent,
        DateFormatPipe,
        EllipsisPipe
    ],
    imports: [
        AppMaterialModule,
        TagInputModule,
        NgUploaderModule
    ],
    providers: [ ApplicationState, AppDataService ],
    entryComponents: [ ConfirmationDialogComponent ]
};
