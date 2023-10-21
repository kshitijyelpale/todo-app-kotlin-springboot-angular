import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Todo } from 'src/app/models/todo.model';
import { TodoService } from 'src/app/services/todo.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  searchTerm = '';
  todos: Todo[] = [];
  filteredTodos: Todo[] = [];

  constructor(
    private readonly router: Router,
    private readonly todoService: TodoService
  ) { }

  ngOnInit() {
    // Fetch todos from your service and populate the 'todos' array.
    this.todoService.getTodos().subscribe((data) => {
      this.todos = data?._embedded?.todoResources;
      this.filteredTodos = this.todos;
    });
  }

  searchTodos(event: Event) {
    this.filteredTodos = this.todos.filter(todo =>
      todo.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  createTodo() {
    this.router.navigate(['/todos/create']).then();
  }

  editTodo(todo: Todo) {
    this.router.navigate(['/todos/', todo.id]).then();
  }

  deleteTodo(todo: Todo) {
    this.todoService.deleteTodo(todo.id).subscribe((data) => {
      this.ngOnInit();
    });
  }
}
