import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseNextComponent } from './choose-next.component';

describe('ChooseNextComponent', () => {
  let component: ChooseNextComponent;
  let fixture: ComponentFixture<ChooseNextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseNextComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseNextComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
