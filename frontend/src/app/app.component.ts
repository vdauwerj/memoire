import {Component, OnInit} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {SpecificationLoader} from './service/specification-loader.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    currentStep$: Observable<number>;
    savedModalVisibility: boolean;
    uploadModalVisibility: boolean;
    selectedAlgebra: any;
    algebraValue = '';
    specName = '';

    constructor(private appFacade: SpecificationLoader, private translateService: TranslateService) {
        this.translateService.currentLang = this.translateService.defaultLang;
    }

    ngOnInit(): void {
        this.currentStep$ = this.appFacade.getCurrentStep();
    }

    isCurrentLang(lang: string): boolean {
        return this.translateService.currentLang === lang;
    }

    selectLang(lang: string) {
        this.translateService.use(lang);
    }
}
