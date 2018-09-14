import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app.module.routes';
import { sharedConfig } from './app.module.shared';

@NgModule({
    bootstrap: sharedConfig.bootstrap,
    declarations: sharedConfig.declarations,
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        HttpModule,
        AppRoutingModule,
        ...sharedConfig.imports
    ],
    providers: [
        ...sharedConfig.providers
    ],
    entryComponents: sharedConfig.entryComponents
})
export class AppModule {
}
