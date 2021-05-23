import {EventEmitter} from '@angular/core';
import {Component, Input, OnInit, Output} from '@angular/core';
import {Action} from '../../../model/action';
import {SpecificationExecutorService} from '../../../service/specification-executor.service';
import {TextSpecification} from '../../../model/text-specification';

@Component({
    selector: 'app-choose-next',
    templateUrl: './choose-next.component.html',
    styleUrls: ['./choose-next.component.scss']
})
export class ChooseNextComponent {

    @Input() set choose(actions: Action[]) {
        if (actions && actions.length > 0) {
            this.specificationExecutorService.getSpecificationString(actions).subscribe(textSpecification => {
                this.textSpecification =
                    textSpecification.map(tspec => ( {...tspec, actions: actions.find(action => JSON.stringify(action.unique) === JSON.stringify(tspec.unique))  }));
            });
        }
    }
    @Output() actionChosen = new EventEmitter<Action>();

    textSpecification: TextSpecification[];

    constructor(private specificationExecutorService: SpecificationExecutorService) {
    }


}
