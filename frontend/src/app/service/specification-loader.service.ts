import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {Observable} from 'rxjs';
import {distinctUntilChanged, map, tap} from 'rxjs/operators';
import {SpecificationExecutorService} from './specification-executor.service';
import {BackendApi} from '../api/backend.api';
import {ExecutionInformation} from '../model/ExecutionInformation';

@Injectable({
    providedIn: 'root'
})
export class SpecificationLoader {

    private readonly state$ = new BehaviorSubject<AppState>(defaultAppState);
    private readonly LOCAL_STORAGE_KEY = 'specifications';

    constructor(private specificationExecutorService: SpecificationExecutorService, private backendApi: BackendApi) {
        const specifications = localStorage.getItem(this.LOCAL_STORAGE_KEY);
        if (specifications) {
            this.updateState({...this.state$.value, savedSpecifications: JSON.parse(specifications)});
        }
        this.state$.asObservable().pipe(
            map(state => state.savedSpecifications),
            distinctUntilChanged()
        ).subscribe(savedSpecification =>
            localStorage.setItem(this.LOCAL_STORAGE_KEY, JSON.stringify(savedSpecification))
        );
    }

    getCurrentStep(): Observable<number> {
        return this.state$.asObservable().pipe(
            map(state => state.currentStep),
            distinctUntilChanged()
        );
    }

    getErrorMessage(): Observable<string> {
        return this.state$.asObservable().pipe(
            map(state => state.errorMessage),
            distinctUntilChanged()
        );
    }

    goStep(step: number) {
        this.updateState({...this.state$.value, currentStep: step});
    }

    getSpecifications(): { name: string, value: string }[] {
        return this.state$.value.savedSpecifications;
    }

    delete(name: string) {
        const currentSpec = this.state$.value;
        this.updateState({...currentSpec, savedSpecifications: currentSpec.savedSpecifications
                .filter(elemn => elemn.name !== name) });
    }

    save(specification: { name: string, value: string }) {
        const savedSpecification = this.state$.value.savedSpecifications;
        let newSpecifications;
        const oldSpecification = savedSpecification.find(spec => spec.name === specification.name);
        if (oldSpecification) {
            newSpecifications = savedSpecification.map(spec =>
                oldSpecification === spec ? {name: spec.name, value: specification.value} : spec);
        } else {
            newSpecifications = [...savedSpecification, specification];
        }
        this.updateState({...this.state$.value, savedSpecifications: newSpecifications});
    }

    existSpecification(specName: string): boolean {
        return !!this.state$.value.savedSpecifications.find(spec => spec.name === specName);
    }

    submit(currentSpecification: string): Observable<ExecutionInformation> {
        return this.backendApi.submitSpecification(currentSpecification)
            .pipe(
                tap(info => {
                    if (!info.errorMessage) {
                        this.specificationExecutorService.reset();
                        this.specificationExecutorService.load(info);
                        this.goStep(2);
                        this.updateErrorMessage('');
                    } else {
                        this.updateErrorMessage(info.errorMessage);
                    }
                })
            );
    }

    private updateState(newState: AppState) {
        this.state$.next(newState);
    }

    private updateErrorMessage(newErrorMessage: string) {
        this.updateState({...this.state$.value, errorMessage: newErrorMessage});
    }

}

interface AppState {
    currentStep: number;
    savedSpecifications: { name: string, value: string }[];
    errorMessage: string;
}

const defaultAppState: AppState = {
    currentStep: 1,
    savedSpecifications: [],
    errorMessage: null
};
