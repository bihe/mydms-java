import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatInputModule, MatSnackBarModule, MatDialogModule, MatNativeDateModule, MatProgressSpinnerModule,
    MatTooltipModule } from '@angular/material';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';


import { TagInputModule } from 'ngx-chips';
import { NgUploaderModule } from 'ngx-uploader';

import { AppComponent } from './components/app/app.component';
import { EllipsisPipe } from './shared/pipes/ellipsis';
import { DateFormatPipe } from './shared/pipes/date.format';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';

import { ApplicationState } from './shared/services/app.state';
import { AppDataService } from './shared/services/app.data.service';
import { DocumentComponent } from './components/document/document.component';
import { ConfirmationDialogComponent } from './shared/confirmation/confirmation.component';

@NgModule({
  imports: [ MatInputModule, MatSnackBarModule, MatDialogModule, MatNativeDateModule, MatProgressSpinnerModule,
    MatTooltipModule, MatChipsModule, MatCardModule, MatSlideToggleModule, MatButtonModule ],
  exports: [ MatInputModule, MatSnackBarModule, MatDialogModule, MatNativeDateModule, MatProgressSpinnerModule,
    MatTooltipModule, MatChipsModule, MatCardModule, MatSlideToggleModule, MatButtonModule ],
})
export class AppMaterialModule { }

export const sharedConfig: NgModule = {
    bootstrap: [ AppComponent ],
    declarations: [
        AppComponent,
        NavbarComponent,
        HomeComponent,
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
