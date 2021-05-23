import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessTraceComponent } from './process-trace.component';

describe('ProcessTraceComponent', () => {
  let component: ProcessTraceComponent;
  let fixture: ComponentFixture<ProcessTraceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessTraceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessTraceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
