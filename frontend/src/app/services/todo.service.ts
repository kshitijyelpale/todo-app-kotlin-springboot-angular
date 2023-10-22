import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { HypermediaService } from './hypermedia.service';
import { TodoResources, Todo } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private apiRoot = 'http://localhost:9090/todos';

  constructor(private readonly hypermediaService: HypermediaService) {
  }

  getTodos(filter: string = ''): Observable<TodoResources> {
    const url = `${this.apiRoot}${filter ? `?${filter}` : ''}`;
    return this.hypermediaService.fetch<TodoResources>(url);
  }

  getCountOfTodos(): Observable<number> {
    return this.hypermediaService.fetch<number>(this.apiRoot + '/count');
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

  updateTaskStatus(todoId: number, taskId: number, status: boolean): Observable<boolean> {
    return this.hypermediaService.update<boolean>(`${this.apiRoot}/${todoId}/task/${taskId}?status=${status}`);
  }

  deleteTodo(todoId: number): Observable<boolean> {
    return this.hypermediaService.remove<boolean>(`${this.apiRoot}/${todoId}`);
  }
}
