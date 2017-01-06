import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingsComponent } from './settings.component';
import { SettingsRoutingModule } from './settings-routing.module';
import { SharedModule } from '../shared/shared.module';

import { GrowlModule, InputSwitchModule } from 'primeng/primeng';

@NgModule({
  imports: [CommonModule, SettingsRoutingModule, SharedModule, GrowlModule, InputSwitchModule],
  declarations: [SettingsComponent],
  exports: [SettingsComponent]
})
export class SettingsModule { }
