import {Component, Input, OnInit} from '@angular/core';
import {SpecificationExecutorService} from '../../../service/specification-executor.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-process-trace',
  templateUrl: './process-trace.component.html',
  styleUrls: ['./process-trace.component.scss']
})
export class ProcessTraceComponent implements OnInit {

  @Input() process: string;
  executedActions$: Observable<string[]>;
  constructor(private specificationExecutorService: SpecificationExecutorService) { }

  ngOnInit(): void {
    this.executedActions$ = this.specificationExecutorService.getExecutedActions(this.process);

  }

}
