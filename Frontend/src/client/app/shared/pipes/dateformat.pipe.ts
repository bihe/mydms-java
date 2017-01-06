import { Pipe,PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({
    name: 'dfmt'
})
export class DateFormatPipe implements PipeTransform {
  transform(val:any, args:any[]) {
    if (args === undefined) {
      return val;
    }

    try {
      let date:string = val.toString();
      let format:string = args.toString();
      let dateFormat = moment(date).format(format);
      return dateFormat;
    } catch(EX) {
      console.error('Could not format date: ' + EX);
    }
    return val;
  }
}
