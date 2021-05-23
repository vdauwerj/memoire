import {Action} from './action';

export interface ProcessDefinition {
    name: string;
    actions: Action[];
}
