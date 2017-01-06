import { Message } from 'primeng/primeng';

export class BaseComponent {

   msgs: Message[] = [];

   protected showErrorMessage(title:string, message:string) {
     this.msgs = [];
     this.msgs.push({severity:'error', summary:title, detail:message});
   }
}
