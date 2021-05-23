import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {distinctUntilChanged, map} from 'rxjs/operators';
import {BackendApi} from '../api/backend.api';
import {ExecutionInformation} from '../model/ExecutionInformation';
import {ProcessDefinition} from '../model/process-definition';
import {Actions} from '../model/actions';
import {Action} from '../model/action';
import {NextExecutableItems} from '../model/next-executable-items';
import {TextSpecification} from '../model/text-specification';

export interface Item {
    processId: number;
    name: string;
    id: number[];
}

export interface State {
    processes: ProcessDefinition[];
    actionPossible: Action[];
    actionsPossible: Actions[];
    currentMulti: Action[];
    liftPosition: Map<string, number>;
    oldState: State[];
    executedActionsPerProcess: Map<string, string[]>;
    executedActions: { action: string, state: State }[];
    currentSpecification: object;
}

const defaultState: State = {
    processes: [],
    actionPossible: [],
    actionsPossible: [],
    currentMulti: [],
    liftPosition: new Map(),
    oldState: [],
    executedActionsPerProcess: new Map(),
    executedActions: [],
    currentSpecification: null
};


@Injectable({
    providedIn: 'root'
})
export class SpecificationExecutorService {

    private state$ = new BehaviorSubject<State>({...defaultState});

    constructor(private backendApi: BackendApi) {
    }

    load(info: ExecutionInformation) {
        const currentPosition = new Map();
        info.processes.map(process => currentPosition.set(process.name, 0));
        this.updateState({
            ...this.state$.value,
            processes: info.processes,
            actionPossible: info.nextExecutableItems.single,
            actionsPossible: info.nextExecutableItems.multi,
            liftPosition: currentPosition,
            executedActionsPerProcess: new Map(),
            currentSpecification: info.currentSpecification,
            executedActions: []
        });

    }

    select(uniques: number[], actions?: Action[]) {
        const state = this.state$.value;
        this.backendApi.select(uniques, state.currentSpecification, state.processes).subscribe(next => {
            this.onNext(next, actions);
        });
    }


    selectMulti(action: Action) {
        this.updateState({...this.state$.value, currentMulti: [...this.state$.value.currentMulti, action]});
    }

    selectParal() {
        const actions = [];
        let uniques = [];
        this.state$.value.currentMulti.forEach(action => {
            actions.push(action);
            uniques = uniques.concat(action.unique);
        });
        this.select(uniques, actions);
    }

    back() {
        const executedActions = this.state$.value.executedActions;
        this.updateState(executedActions[executedActions.length - 1].state);
    }

    getSpecificationString(actions: Action[]): Observable<TextSpecification[]> {
        return this.backendApi.getStringSpecification(actions, this.state$.value.currentSpecification);
    }

    getProcesses$(): Observable<ProcessDefinition[]> {
        return this.state$.pipe(
            map(state => state.processes),
            distinctUntilChanged()
        );
    }

    getSinglePossible(processName: string): Observable<Action[]> {
        return this.state$.pipe(
            map(state => state.actionPossible.filter(action => action.process === processName))
        );
    }

    getMultiPossible(processName: string): Observable<Actions[]> {
        return this.state$.pipe(
            map(state => {
                const currentMulti = this.state$.value.currentMulti;
                if (currentMulti.length === 0) {
                    return state.actionsPossible;
                }
                return state.actionsPossible.filter(actions => this.containsAllCurrentMulti(currentMulti, actions.actions));
            }),
            map(actions => actions.map(action => ({
                    ...action,
                    actions: action.actions.filter(action => action.process === processName)
                })
            ))
        );
    }


    getCurrentMulti(processName: string): Observable<Action[]> {
        return this.state$.pipe(
            map(state => state.currentMulti),
            map(actions => actions.filter(act => act.process === processName)),
            distinctUntilChanged()
        );
    }


    getCurrentLiftPosition(processName: string): Observable<number> {
        return this.state$.pipe(
            map(state => state.liftPosition),
            map(liftPosition => liftPosition.get(processName)),
            distinctUntilChanged()
        );
    }

    getExecutedActions(process: string): Observable<string[]> {
        return this.state$.pipe(
            map(state => state.executedActionsPerProcess),
            map(executedActions => executedActions.get(process)),
            map(executedActions => executedActions ? executedActions : []),
            distinctUntilChanged()
        );
    }

    getAllExecutedActions(): Observable<{ action: string, state: State }[]> {
        return this.state$.pipe(
            map(state => state.executedActions),
            distinctUntilChanged()
        );
    }

    isBackPossible$(): Observable<boolean> {
        return this.state$.pipe(
            map(state => state.executedActions.length > 0)
        );
    }

    random(): Observable<void> {
        return of(void 0);
    }

    reset() {
        this.updateState(defaultState);
    }

    isMultiPossible() {
        return this.state$.pipe(
            map(state => state.actionsPossible),
            map(actions => actions.length > 0)
        );
    }

    backTo(state: State) {
        this.updateState(state);
    }

    private getGlobalExecutedActions(actions: Action[]): string {
        if (!actions || actions.length === 0) {
            return null;
        }
        let ret = '';
        for (let i = 0; i < actions.length; i++) {
            const action = actions[i];
            const indexOfPar = action.name.indexOf('|');
            if (indexOfPar > -1) {
                ret += action.process + ': ' +
                    action.name.substr(0, indexOfPar) + '  | ' + action.process + ': '
                    + action.name.substr(indexOfPar + 1, action.name.length);
            } else {
                ret += action.process + ':' + action.name;
            }
            if (actions.length - 1 !== i) {
                ret += ' | ';
            }
        }
        return ret;
    }

    private onNext(next: NextExecutableItems, actions?: Action[]) {
        const currentState = {...this.state$.value, currentMulti: []};
        const newExecutedActionsPerProcess = new Map(currentState.executedActionsPerProcess);
        (actions || [])
            .forEach(action => {
                const executedAction = newExecutedActionsPerProcess.get(action.process);
                newExecutedActionsPerProcess.set(action.process, executedAction ? [...executedAction, action.name] : [action.name]);
            });
        const globalExecutedActions = {action: this.getGlobalExecutedActions(actions), state: currentState};
        const executedActions = [...currentState.executedActions, globalExecutedActions];

        this.updateState({
            ...currentState,
            actionPossible: next.single,
            actionsPossible: next.multi,
            oldState: currentState.oldState ? [...currentState.oldState, currentState] : [currentState],
            liftPosition: actions ? this.getLiftPosition(actions) : currentState.liftPosition,
            executedActionsPerProcess: newExecutedActionsPerProcess,
            executedActions: executedActions,
            currentSpecification: next.currentSpecification
        });

    }

    private updateState(toUpdate: State) {
        this.state$.next(toUpdate);
    }

    private containsAllCurrentMulti(currentMulti: Action[], actions: Action[]) {
        let ret = true;
        currentMulti.forEach(multi => {
            const found = actions.find(action => action.name === multi.name && action.process === multi.process);
            if (!found) {
                ret = false;
                return;
            }
        });
        return ret;
    }

    private getLiftPosition(actions: Action[]) {
        const currentPosition = new Map<string, number>(this.state$.value.liftPosition);
        actions.forEach(action => {
            const processDefinition = this.state$.value.processes.find(process => process.name === action.process);
            const liftPosition = processDefinition.actions.findIndex(curr => curr.name === action.name);
            currentPosition.set(processDefinition.name, liftPosition + 1);
        });
        return currentPosition;

    }

}

export const mockSpecification = {
    id: {
        name: 'Preparation',
        term: 'processId'
    },
    process: {
        op: '.',
        left: {
            name: 'prendreOeufs',
            term: 'action',
        },
        right: {
            op: '.',
            left: {
                name: 'casserOeufs',
                term: 'action',
            },
            right: {
                id: {
                    name: 'Cuire',
                    term: 'processId'
                },
                process: {
                    op: '.',
                    left: {
                        name: 'chaufferOeufs',
                        term: 'action',
                    },
                    right: {
                        op: '+',
                        left: {
                            name: 'retirerPoelle',
                            term: 'action',
                        },
                        right: {
                            name: 'laisserCramer',
                            term: 'action',
                        },
                        term: 'composition'
                    },
                    term: 'composition'
                },
                term: 'processSpec'
            },
            term: 'composition'
        },
        term: 'composition'
    },
    term: 'processSpec'
};
