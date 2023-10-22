import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';

import {catchError, EMPTY, forkJoin, map, switchMap, take, tap} from 'rxjs';

import { Todo } from 'src/app/models/todo.model';
import { TodoService, SnackbarService } from 'src/app/services';
import { ConfirmationDialogComponent }  from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-todo-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  searchTerm = '';
  showResetButton = false;
  pageSize = 10;
  currentPage = 0;
  totalCount = 0;
  todos: Todo[] = [];

  constructor(
    private readonly router: Router,
    private readonly dialog: MatDialog,
    private readonly todoService: TodoService,
    private readonly snackbar: SnackbarService
  ) { }

  ngOnInit() {
    this.searchTodos();
  }

  searchTodos(event?: Event): void {
    const filter = `filter=${this.searchTerm.toLowerCase()}&page=${this.currentPage}&size=${this.pageSize}`;

    forkJoin([
      this.todoService.getTodos(filter).pipe(take(1)),
      this.todoService.getCountOfTodos().pipe(take(1))
    ]).subscribe(([todosResponse, count]) => {
      this.todos = todosResponse?._embedded?.todoResources;
      this.showResetButton = this.searchTerm.length > 0;
      this.totalCount = count;
    });
  }

  createTodo(): void {
    this.router.navigate(['todos', 'create']).then();
  }

  viewTodo(todo: Todo): void {
    this.router.navigate(['todos', todo.id, 'view']).then();
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
        this.ngOnInit();
        this.snackbar.showSuccess('Todo successfully deleted!');
      }),
      catchError((err: Error) => {
        this.snackbar.showError('Deletion failed - ' + err.message);
        return EMPTY;
      })
    ).subscribe();
  }

  resetSearchTerm() {
    this.searchTerm = '';
    this.searchTodos();
    this.showResetButton = false;
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event?.pageIndex;
    this.pageSize = event?.pageSize;
    this.searchTodos();
  }
}
