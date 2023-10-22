import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { catchError, delay, EMPTY, map, Observable, take, tap } from 'rxjs';

import { SnackbarService, TodoService } from 'src/app/services';
import { Todo, Task } from 'src/app/models';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-todo-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.scss']
})
export class ViewComponent implements OnInit {

  todo$: Observable<Todo> = EMPTY;
  todoId = 0;
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly dialog: MatDialog,
    private readonly todoService: TodoService,
    private readonly snackbar: SnackbarService
  ) {
  }

  ngOnInit(): void {
    this.todoId = this.route.snapshot.params['todoId'] as number;
    this.todo$ = this.todoService.getTodoById(this.todoId).pipe(
      delay(200), // To observe the loading indicator
      map((todo) => {
        todo.tasks = todo.tasks.sort((a, b) => a.completed === b.completed ? 0 : a.completed ? 1 : -1);
        return todo;
      }),
      tap(() => this.loading = false)
    );
    this.loading = true;
  }

  editTodo(todo: Todo): void {
    this.router.navigate(['todos', todo.id, 'edit']).then();
  }

  confirmDeletion(todo: Todo) {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'Are you sure you want to delete this Todo?'
    });

    dialogRef.afterClosed().pipe(
      take(1),
      map((result) => {
        if (result === true) {
          this.deleteTodo(todo);
        }
      })
    ).subscribe();
  }

  deleteTodo(todo: Todo): void {
    this.todoService.deleteTodo(todo.id).pipe(
      take(1),
      tap(() => {
        this.router.navigate(['/todos']).then();
        this.snackbar.showSuccess('Todo successfully deleted!');
      }),
      catchError((err: Error) => {
        this.snackbar.showError('Deletion failed - ' + err.message);
        return EMPTY;
      })
    ).subscribe();
  }

  updateTaskStatus(todo: Todo, task: Task): void {
    const originalStatus = task.completed;

    this.todoService.updateTaskStatus(todo.id, task.id, !task.completed)
      .pipe(
        take(1),
        map(() => {
          task.completed = !task.completed;
          todo.tasks = todo.tasks.sort((a, b) => a.completed === b.completed ? 0 : a.completed ? 1 : -1);
        }),
        catchError(() => {
          task.completed = originalStatus;
          return EMPTY;
        })
      ).subscribe();
  }
}
