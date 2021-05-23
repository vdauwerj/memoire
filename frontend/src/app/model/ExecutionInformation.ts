import {ProcessDefinition} from './process-definition';
import {NextExecutableItems} from './next-executable-items';

export interface ExecutionInformation {
    processes: ProcessDefinition[];
    nextExecutableItems: NextExecutableItems;
    errorMessage: string;
    currentSpecification: object;
}
