import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '@angular/material';
import { SettingsComponent } from './settings.component';
import { SettingsRoutingModule } from './settings-routing.module';
import { SharedModule } from '../shared/shared.module';

import { GrowlModule, InputSwitchModule } from 'primeng/primeng';

@NgModule({
  imports: [CommonModule, SettingsRoutingModule, SharedModule, GrowlModule, InputSwitchModule, MaterialModule.forRoot()],
  declarations: [SettingsComponent],
  exports: [SettingsComponent]
})
export class SettingsModule { }
