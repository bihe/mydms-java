import { Component } from '@angular/core';
import { DataModel } from '../models/data.model';

@Component({
  moduleId: module.id,
  selector: 'mydms-progress',
  templateUrl: 'progress.component.html',
  styleUrls: ['progress.component.css']
})
export class ProgressComponent {

  constructor(private data: DataModel) {}

    isActive():boolean {
        return this.data.getIsActive();
    }
}
