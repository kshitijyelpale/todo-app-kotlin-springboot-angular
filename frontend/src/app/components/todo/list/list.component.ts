import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { Todo } from 'src/app/models/todo.model';
import { TodoService } from 'src/app/services/todo.service';
import { ConfirmationDialogComponent}  from '../../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-todo-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  searchTerm = '';
  todos: Todo[] = [];
  filteredTodos: Todo[] = [];

  constructor(
    private readonly router: Router,
    private readonly dialog: MatDialog,
    private readonly todoService: TodoService
  ) { }

  ngOnInit() {
    this.todoService.getTodos().subscribe((data) => {
      this.todos = data?._embedded?.todoResources;
      this.filteredTodos = this.todos;
    });
  }

  searchTodos(event: Event): void {
    this.filteredTodos = this.todos.filter(todo =>
      todo.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
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

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteTodo(todo);
      }
    });
  }

  deleteTodo(todo: Todo): void {
    this.todoService.deleteTodo(todo.id).subscribe(() => {
      this.ngOnInit();
    });
  }
}
