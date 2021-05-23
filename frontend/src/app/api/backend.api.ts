import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {ExecutionInformation} from '../model/ExecutionInformation';
import {NextExecutableItems} from '../model/next-executable-items';
import {Action} from '../model/action';
import {TextSpecification} from '../model/text-specification';
import {ProcessDefinition} from '../model/process-definition';

@Injectable({
    providedIn: 'root'
})
export class BackendApi {


    constructor(private httpClient: HttpClient) {
    }

    submitSpecification(specification: string): Observable<ExecutionInformation> {
        return this.httpClient.post<ExecutionInformation>('/api/v2/analyze/send', {code: specification});
    }

    select(id: number[], currentSpecification: object, processes: ProcessDefinition[]): Observable<NextExecutableItems> {
        return this.httpClient.post<NextExecutableItems>('/api/v2/analyze/select', {
            unique: id,
            currentSpecification: currentSpecification,
            processes: processes
        });
    }

    back() {
        return this.httpClient.get<NextExecutableItems>('/api/v2/analyze/back');
    }

    getStringSpecification(actions: Action[], currentSpecification: object): Observable<TextSpecification[]> {
        return this.httpClient.post<TextSpecification[]>('/api/v2/analyze/specification', { actions: actions, currentSpecification: currentSpecification});
    }
}
