import { Component, OnInit } from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';

import { Todo } from 'src/app/models/todo.model';
import { Task } from 'src/app/models/task.model';
import { TodoService } from 'src/app/services/todo.service';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  todo = {} as Todo;
  task = {} as Task;

  isCreate = false;
  todoId = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly todoService: TodoService
  ) {
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  ngOnInit(): void {
    this.todoId = this.route.snapshot.params['todoId'] as number;
    if (this.todoId) {
      this.todoService.getTodoById(this.todoId).subscribe((data) => {
        this.todo = data;
      });
    }
    else {
      this.isCreate = true;
    }
  }

  addTask() {
    const newTask = { id: 0, name: '', description: '', completed: false };
    this.todo.tasks.push({ ...newTask });
  }

  removeTask(index: number) {
    this.todo.tasks.splice(index, 1);
  }

  createTodo() {
    // Implement logic to save the todo
  }

  saveTodo() {
    this.todoService.saveTodo(this.todo).subscribe((data) => {
      this.todo = data;
      this.router.navigate(['/todos']).then();
    });
  }

  updateTodo() {
    this.todoService.updateTodo(this.todo).subscribe((data) => {
      this.todo = data;
      this.router.navigate(['/todos']).then();
    });
  }

  deleteTodo() {
    this.todoService.deleteTodo(this.todo.id).subscribe((data) => {
      this.router.navigate(['/todos']).then();
    });
  }
}
