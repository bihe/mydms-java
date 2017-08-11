import { ReplaySubject } from 'rxjs/ReplaySubject';
import { ApplicationData } from '../models/application.data';

export class ApplicationState {
    private appData: ReplaySubject<ApplicationData> = new ReplaySubject();
    private searchInput: ReplaySubject<string> = new ReplaySubject();

    public getAppData(): ReplaySubject<ApplicationData> {
        return this.appData;
    }

    public setAppData(data: ApplicationData) {
        this.appData.next(data);
    }

    public getSearchInput(): ReplaySubject<string> {
        return this.searchInput;
    }

    public setSearchInput(data: string) {
        this.searchInput.next(data);
    }
}
