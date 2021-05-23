import {Component, OnInit} from '@angular/core';
import {SpecificationLoader} from '../service/specification-loader.service';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-step1',
    templateUrl: './step1.component.html',
    styleUrls: ['./step1.component.scss']
})
export class Step1Component implements OnInit {
    savedModalVisibility: boolean;
    uploadModalVisibility: boolean;
    deleteModalVisibility: boolean;
    selectedAlgebra: any;
    currentSpecification = '';
    specName = '';
    errorMessage$: Observable<string>;

    constructor(private specificationFacade: SpecificationLoader) {
    }

    ngOnInit(): void {
        this.errorMessage$ = this.specificationFacade.getErrorMessage();
    }


    onSubmit() {
        this.specificationFacade.submit(this.currentSpecification).subscribe();
    }

    onSave() {
        this.savedModalVisibility = true;
    }

    onSaveModal() {
        this.savedModalVisibility = false;
        this.specificationFacade.save({name: this.specName, value: this.currentSpecification});
    }

    onUpload() {
        this.uploadModalVisibility = true;
    }

    upload() {
        this.currentSpecification = this.selectedAlgebra.value;
        this.specName = this.selectedAlgebra.name;
        this.uploadModalVisibility = false;
    }

    onCancelModal() {
        this.savedModalVisibility = false;
        this.uploadModalVisibility = false;
        this.deleteModalVisibility = false;
    }

    getSpecifications(): { name: string; value: string }[] {
        return this.specificationFacade.getSpecifications();
    }

    specAlreadyExist(specName: string): boolean {
        return this.specificationFacade.existSpecification(specName);
    }

    onDelete() {
        this.deleteModalVisibility = true;
    }

    isButtonDisabled() {
        return this.uploadModalVisibility || this.savedModalVisibility || this.deleteModalVisibility;
    }

    private delete() {
        this.specificationFacade.delete(this.selectedAlgebra.name);
        this.deleteModalVisibility = false;
    }
}
