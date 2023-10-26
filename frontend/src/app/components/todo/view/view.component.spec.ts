import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ActivatedRoute , Router } from '@angular/router';

import { of } from 'rxjs';

import { ViewComponent } from './view.component';
import { SnackbarService, TodoService } from 'src/app/services';
import { TODO } from 'src/app/mocks';
import { ConfirmationDialogComponent } from 'src/app/components/confirmation-dialog/confirmation-dialog.component';

describe('ViewComponent', () => {
  let component: ViewComponent;
  let fixture: ComponentFixture<ViewComponent>;

  const todoServiceSpy = jasmine.createSpyObj('TodoService', ['updateTaskStatus', 'deleteTodo', 'getTodoById']);
  const snackbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['showSuccess', 'showError']);

  const router = {
    navigate: jasmine.createSpy('navigate'),
  };

  const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
  const matDialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['afterClosed']);

  const activatedRoute = {
    snapshot: {
      params: {
        todoId: 1
      }
    },
  };

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [ RouterTestingModule, MatDialogModule, HttpClientTestingModule, MatSnackBarModule ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
      declarations: [ ViewComponent ],
      providers: [
        { provide: TodoService, useValue: todoServiceSpy },
        { provide: SnackbarService, useValue: snackbarServiceSpy },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: MatDialog, useValue: matDialogSpy }
      ]
    });
    todoServiceSpy.updateTaskStatus.and.returnValue(of(true));
    todoServiceSpy.deleteTodo.and.returnValue(of(true));
    todoServiceSpy.getTodoById.and.returnValue(of(TODO));
    snackbarServiceSpy.showError.and.returnValue();
    snackbarServiceSpy.showSuccess.and.returnValue();
    matDialogSpy.open.and.returnValue(matDialogRefSpy);

    fixture = TestBed.createComponent(ViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    todoServiceSpy.updateTaskStatus.calls.reset();
    todoServiceSpy.deleteTodo.calls.reset();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize component and fetch the todo', () => {
    // Act
    component.ngOnInit();

    // Assert
    expect(component.todoId).toBe(1);
    expect(todoServiceSpy.getTodoById).toHaveBeenCalledWith(1);
    expect(component.todo$).toBeDefined();
    expect(component.todo$.subscribe(todo => {
      expect(todo.tasks[0].id).toBe(2);
      expect(todo.tasks[1].id).toBe(1);
      expect(todo.tasks[2].id).toBe(3);
    }));
    expect(component.loading).toBeTruthy();
  });

  it('should navigate to edit page', () => {
    // Act
    component.editTodo(TODO);

    // Assert
    expect(router.navigate).toHaveBeenCalledWith(['todos', TODO.id, 'edit']);
  });

  it('should open confirmation dialog and receive users action', () => {
    // Arrange
    matDialogRefSpy.afterClosed.and.returnValue(of(true));
    const deleteTodoSpy = spyOn(component, 'deleteTodo');

    // Act
    component.confirmDeletion(TODO);

    // Assert
    expect(matDialogSpy.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'Are you sure you want to delete this Todo?'
    });
    expect(deleteTodoSpy).toHaveBeenCalledWith(TODO);
  });

  it('should not delete the todo if confirmation not received from user', () => {
    // Arrange
    matDialogRefSpy.afterClosed.and.returnValue(of(false));
    const deleteTodoSpy = spyOn(component, 'deleteTodo');

    // Act
    component.confirmDeletion(TODO);

    // Assert
    expect(matDialogSpy.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'Are you sure you want to delete this Todo?'
    });
    expect(deleteTodoSpy).not.toHaveBeenCalled();
  });

  it('should delete todo when called', fakeAsync(() => {
    // Act
    component.deleteTodo(TODO);
    tick();

    // Assert
    expect(todoServiceSpy.deleteTodo).toHaveBeenCalledWith(TODO.id);
    expect(router.navigate).toHaveBeenCalledWith(['/todos']);
  }));

  it('should update the task status', fakeAsync(() => {
    // Arrange
    const task = TODO.tasks[1];
    // Act
    component.updateTaskStatus(TODO, task);
    tick();
    // Assert
    expect(todoServiceSpy.updateTaskStatus).toHaveBeenCalledWith(TODO.id, task.id, true);
    expect(task.completed).toBeTruthy();
    expect(TODO.tasks[2]).toBe(task);
  }));
});
