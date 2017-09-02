import { Component, Inject, OnInit } from '@angular/core';
import { MD_DIALOG_DATA, MdDialogRef, MdDialogConfig } from '@angular/material';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.css']
})
export class ConfirmationDialogComponent implements OnInit {

    name = '';

    constructor(
        private dialog: MdDialogRef<ConfirmationDialogComponent>,
        @Inject(MD_DIALOG_DATA) private dialogData: any) {
        this.name = dialogData.name;
    }

    public static getDialogConfig(name: string): MdDialogConfig {
        const dialogConfig: MdDialogConfig = {
            disableClose: false,
            width: '440px',
            height: '240px',
            data: {
                name: name
            }
        };
        return dialogConfig;
    }

    public onConfirmDelete() {
        this.dialog.close(true);
    }

    ngOnInit() {
    }
}
