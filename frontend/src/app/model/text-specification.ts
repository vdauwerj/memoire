import {Action} from './action';

export interface TextSpecification {
     text: string ;
     unique: number[];
     actions?: Action;
}
