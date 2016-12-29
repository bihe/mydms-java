import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home.component';
import { HomeRoutingModule } from './home-routing.module';
import { SharedModule } from '../shared/shared.module';

import { GrowlModule } from 'primeng/primeng';

@NgModule({
  imports: [CommonModule, HomeRoutingModule, SharedModule, GrowlModule],
  declarations: [HomeComponent],
  exports: [HomeComponent]
})
export class HomeModule { }
