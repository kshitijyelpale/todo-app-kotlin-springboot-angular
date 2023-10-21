import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';

import { delay, EMPTY, Observable, of, tap } from 'rxjs';

import { Todo, Task, LinkResource } from 'src/app/models';
import { TodoService } from 'src/app/services';

@Component({
  selector: 'app-todo-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  todo$: Observable<Todo> = EMPTY;

  isCreate = false;
  todoId = 0;
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly location: Location,
    private readonly dialog: MatDialog,
    private readonly todoService: TodoService
  ) {
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  ngOnInit(): void {
    this.todoId = this.route.snapshot.params['todoId'] as number;
    if (this.todoId) {
      this.todo$ = this.todoService.getTodoById(this.todoId).pipe(
        delay(200),
        tap(() => this.loading = false)
      );
      this.loading = true;
      return;
    }
    this.isCreate = true;
    this.todo$ = of({
      id: 0,
      name: '',
      description: '',
      tasks: [] as Task[],
      _links: {} as LinkResource
    });
  }

  addTask(todo: Todo) {
    console.info(todo);
    const newTask = { id: 0, name: '', description: '', completed: false };
    todo.tasks.push(newTask);
  }

  removeTask(todo: Todo, index: number) {
    todo.tasks.splice(index, 1);
  }

  saveTodo(todo: Todo) {

    this.todo$ = this.todoService.saveTodo(todo).pipe(
      tap(() => this.router.navigate(['/todos']).then())
    );
  }

  updateTodo(todo: Todo) {
    this.todo$ = this.todoService.updateTodo(todo).pipe(
      tap(() => this.location.back())
    );
  }

  goBack() {
    this.location.back();
  }
}
