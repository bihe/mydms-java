import { ReplaySubject } from 'rxjs/ReplaySubject';
import { ApplicationData } from '../models/application.data';

export class ApplicationState {
    private appData: ReplaySubject<ApplicationData> = new ReplaySubject();

    public getAppData(): ReplaySubject<ApplicationData> {
        return this.appData;
    }

    public setAppData(data: ApplicationData) {
        this.appData.next(data);
    }
}
