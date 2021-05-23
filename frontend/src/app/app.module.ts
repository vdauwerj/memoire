import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {ButtonModule} from 'primeng/button';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {ElevatorComponent} from './step2/elevator/elevator.component';
import {SkeletonModule} from 'primeng/skeleton';
import {KnobModule} from 'primeng/knob';
import {ChartModule} from 'primeng/chart';
import {DropdownModule} from 'primeng/dropdown';
import {DialogModule} from 'primeng/dialog';
import {FormsModule} from '@angular/forms';
import {Step1Component} from './step1/step1.component';
import {Step2Component} from './step2/step2.component';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import { ChooseNextComponent } from './step2/elevator/choose-next/choose-next.component';
import { ProcessTraceComponent } from './step2/elevator/process-trace/process-trace.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';


@NgModule({
    declarations: [
        AppComponent,
        ElevatorComponent,
        Step1Component,
        Step2Component,
        ChooseNextComponent,
        ProcessTraceComponent,
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        InputTextareaModule,
        ButtonModule,
        MessagesModule,
        MessageModule,
        SkeletonModule,
        KnobModule,
        ChartModule,
        DialogModule,
        DropdownModule,
        FormsModule,
        HttpClientModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (http: HttpClient) => new TranslateHttpLoader(http),
                deps: [HttpClient]
            },
            useDefaultLang: true,
            defaultLanguage: 'en'
        }),
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
