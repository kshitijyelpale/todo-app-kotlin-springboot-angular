import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { HypermediaService } from './hypermedia.service';
import { TodoResources } from '../models/todo.resources';
import { Todo } from '../models/todo.model';

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private apiRoot = 'http://localhost:9090/todos';

  constructor(private readonly hypermediaService: HypermediaService) {
  }

  getTodos(): Observable<TodoResources> {
    return this.hypermediaService.fetch<TodoResources>(this.apiRoot);
  }

  getTodoById(id: number): Observable<Todo> {
    return this.hypermediaService.fetch<Todo>(`${this.apiRoot}/${id}`);
  }

  saveTodo(todo: Todo): Observable<Todo> {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return this.hypermediaService.save<Todo>(this.apiRoot, todo);
  }

  updateTodo(todo: Todo): Observable<Todo> {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return this.hypermediaService.update<Todo>(`${this.apiRoot}/${todo.id}`, todo);
  }

  deleteTodo(todoId: number): Observable<boolean> {
    return this.hypermediaService.remove<boolean>(`${this.apiRoot}/${todoId}`);
  }
}
