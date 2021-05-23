import {Action} from './action';
import {Actions} from './actions';

export interface NextExecutableItems{
   single: Action[];
   multi: Actions[];
   currentSpecification: object;
}
