import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '@angular/material';
import { DocumentComponent } from './document.component';
import { DocumentRoutingModule } from './document-routing.module';
import { SharedModule } from '../shared/shared.module';

import { FileUploadModule, MessagesModule, AutoCompleteModule } from 'primeng/primeng';

@NgModule({
  imports: [CommonModule, DocumentRoutingModule, SharedModule, FileUploadModule, AutoCompleteModule, MaterialModule.forRoot()],
  declarations: [DocumentComponent],
  exports: [DocumentComponent]
})
export class DocumentModule { }
