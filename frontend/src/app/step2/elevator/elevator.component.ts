import {Component, Input, OnInit} from '@angular/core';
import {Item, SpecificationExecutorService} from '../../service/specification-executor.service';
import {Observable, of} from 'rxjs';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {delay, filter, first, tap} from 'rxjs/operators';
import {ProcessDefinition} from '../../model/process-definition';
import {Action} from '../../model/action';
import {Actions} from '../../model/actions';

const HEIGHT_OF_ONE_FLOOR = 70;

@Component({
    selector: 'app-elevator',
    templateUrl: './elevator.component.html',
    styleUrls: ['./elevator.component.scss'],
    animations: [
        trigger('moveFloor', [
            state('startMove', style({
                top: '{{pixels}}px',
            }), {params: {pixels: '30'}}),
            state('endMove', style({
                top: '{{pixels}}px',
            }), {params: {pixels: '30'}}),
            transition('startMove => endMove', animate('500ms linear'))
        ]),
        trigger('door', [
            state('open', style({
                width: '1000px'
            })),
            state('close', style({
                width: '5px'
            })),
            transition('* <=> *', animate('500ms linear')),
        ])
    ]
})

export class ElevatorComponent implements OnInit {

    @Input()
    process: ProcessDefinition;
    singleSelectableAction: Action[];
    currentFloor = 0;
    state = 'startMove';
    oldPosition = 0;
    doorState = 'open';
    multiSelectableAction: Actions[];
    currentMulti: Action[];
    chooseNext = {
        visibility: false,
        choose: [],
        multi: false
    };


    constructor(private specificationExecutorService: SpecificationExecutorService) {
    }

    ngOnInit(): void {
        this.specificationExecutorService.getSinglePossible(this.process.name).subscribe(
            items => this.singleSelectableAction = items
        );
        this.specificationExecutorService.getMultiPossible(this.process.name).subscribe(
            items => this.multiSelectableAction = items
        );
        this.specificationExecutorService.getCurrentMulti(this.process.name).subscribe(currentMulti =>
            this.currentMulti = currentMulti
        );


        this.specificationExecutorService.getCurrentLiftPosition(this.process.name)
            .subscribe((index) => this.moveElevator(index));
    }

    selectAction(action: string) {
        const actions = this.findActionByName(action);
        if (actions.length === 1) {
            this.specificationExecutorService.select(actions[0].unique, [actions[0]]);
        } else {
            this.chooseNext = {
                visibility: true,
                choose: actions,
                multi: false
            };
        }
    }

    onActionChosen(action: Action) {
        if (this.chooseNext.multi) {
            this.specificationExecutorService.selectMulti(action);
        } else {
            this.specificationExecutorService.select(action.unique, [action]);
        }
        this.chooseNext = {
            visibility: false,
            choose: [],
            multi: false
        };
    }

    selectMulti(action: Action) {
        const actions = this.findActionByName(action.name);
        if (actions.length === 1) {
            this.specificationExecutorService.selectMulti(actions[0]);
        } else {
            this.chooseNext = {
                visibility: true,
                choose: actions,
                multi: true
            };
        }


    }

    isNextAction(action: Action): boolean {
        return this.findActionByName(action.name).length > 0;
    }

    isMultiAction(toValidate: Action): boolean {
        return !!this.multiSelectableAction.find(multi => !!multi.actions.find(action => action.name === toValidate.name)) && !this.isCurrentMulti(toValidate.name);
    }

    isCurrentMulti(actionName: string): boolean {
        return !!this.currentMulti.find(multi => multi.name === actionName);
    }

    private findActionByName(actionName: string): Action[] {
        return this.singleSelectableAction.filter(current => current.name === actionName);
    }

    private moveElevator(newFloor: number) {
        of('').pipe(
            tap(() => this.doorState = 'close'),
            delay(700),
            tap(() => {
                this.oldPosition = this.currentFloor * HEIGHT_OF_ONE_FLOOR;
                this.currentFloor = newFloor;
                this.state = 'startMove';
            }),
            delay(200),
            tap(() => this.state = 'endMove'),
            delay(500),
            tap(() => this.doorState = 'open'),
            first()
        ).subscribe();
    }


}
