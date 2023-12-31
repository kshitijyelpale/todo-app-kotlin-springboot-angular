package com.example.todoapp.controller

import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.service.ITaskService
import com.example.todoapp.service.ITodoService
import com.example.todoapp.service.TodoResourceAssembler
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
@Validated
class TodoController(
    private val todoService: ITodoService,
    private val taskService: ITaskService,
    private val todoResourceAssembler: TodoResourceAssembler
) {

    @GetMapping
    fun getAllTodos(
        @RequestParam(defaultValue = "") filter: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): CollectionModel<EntityModel<TodoResource>> {
        val pageable = PageRequest.of(page, size)
        return todoService.findAllTodos(pageable, filter)
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long) = todoResourceAssembler.toModel(todoService.findTodoById(id))

    @PostMapping
    fun createTodo(@RequestBody @Valid resource: TodoResource) =
        todoResourceAssembler.toModel(todoService.createTodo(resource))

    @PutMapping("/{id}")
    fun updateTodo(@PathVariable id: Long, @RequestBody @Valid resource: TodoResource) =
        todoResourceAssembler.toModel(todoService.updateTodo(id, resource))

    @PutMapping("/{id}/task/{taskId}")
    fun updateTaskStatus(@PathVariable id: Long, @PathVariable taskId: Long, @RequestParam("status") status: Boolean) =
        ResponseEntity.ok(taskService.updateTaskStatus(id, taskId, status))

    @GetMapping("/count")
    fun getCountOfTodos(): Long = todoService.getTotalCountOfTodos()

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long) = todoService.deleteTodoById(id)

    @GetMapping("/over-due-date")
    fun getDueDatedTodos() = todoService.getDueDatedTodos()
}
