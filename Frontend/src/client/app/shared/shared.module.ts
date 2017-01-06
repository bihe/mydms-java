import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { NavbarComponent } from './navbar/index';
import { ProgressComponent } from './progress/index';
import { BackendService } from './backend/index';
import { EllipsisPipe, DateFormatPipe } from './pipes/index';

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */

@NgModule({
  imports: [CommonModule, RouterModule],
  declarations: [NavbarComponent,
    ProgressComponent,
    EllipsisPipe,
    DateFormatPipe
    ],
  exports: [NavbarComponent,
    CommonModule,
    FormsModule,
    RouterModule,
    ProgressComponent,
    EllipsisPipe,
    DateFormatPipe
    ]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule,
      providers: [BackendService]
    };
  }
}
