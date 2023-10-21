import {Component, OnDestroy, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog} from '@angular/material/dialog';

import { delay, EMPTY, map, Observable, Subscription, tap } from 'rxjs';

import { TodoService } from 'src/app/services';
import { Todo } from 'src/app/models';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-todo-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.scss']
})
export class ViewComponent implements OnInit, OnDestroy {

  todo$: Observable<Todo> = EMPTY;
  todoId = 0;
  sub$ = new Subscription();
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly dialog: MatDialog,
    private readonly todoService: TodoService
  ) {
  }

  ngOnInit(): void {
    this.todoId = this.route.snapshot.params['todoId'] as number;
    this.todo$ = this.todoService.getTodoById(this.todoId).pipe(
      delay(500), // To observe the loading indicator
      map((todo) => {
        todo.tasks = todo.tasks.map((task) => ({ ...task, completed: false }));
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

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteTodo(todo);
      }
    });
  }

  deleteTodo(todo: Todo): void {
    this.sub$.add(
      this.todoService.deleteTodo(todo.id).subscribe(() => {
        this.router.navigate(['/todos']).then();
      })
    );
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe();
  }
}
