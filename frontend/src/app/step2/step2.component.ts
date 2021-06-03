import {Component, OnInit} from '@angular/core';
import {Item, SpecificationExecutorService, State} from '../service/specification-executor.service';
import {Observable} from 'rxjs';
import {ProcessDefinition} from '../model/process-definition';
import {Action} from '../model/action';

@Component({
    selector: 'app-step2',
    templateUrl: './step2.component.html',
    styleUrls: ['./step2.component.scss']
})
export class Step2Component implements OnInit {

    allProcess$: Observable<ProcessDefinition[]>;
    executedActions$: Observable<{action: string, state: State}[]>;
    backPossible$: Observable<boolean>;
     multiPossible$: Observable<boolean>;
    trackByElevator = (index: number, processDefinition: ProcessDefinition) => processDefinition.name;

    constructor(private specificationExecutorService: SpecificationExecutorService) {
    }

    ngOnInit(): void {
        this.allProcess$ = this.specificationExecutorService.getProcesses$();
        this.executedActions$ = this.specificationExecutorService.getAllExecutedActions();
        this.backPossible$ = this.specificationExecutorService.isBackPossible$();
        this.multiPossible$ = this.specificationExecutorService.isMultiPossible();
    }

    back() {
        this.specificationExecutorService.back();
    }

    random() {
        this.specificationExecutorService.random();
    }


    selectParalelle() {
        this.specificationExecutorService.selectParal();
    }

    backTo(state: State) {
        this.specificationExecutorService.backTo(state);
    }
}
