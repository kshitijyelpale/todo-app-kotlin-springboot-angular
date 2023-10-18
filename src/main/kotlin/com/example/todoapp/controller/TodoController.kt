package com.example.todoapp.controller

import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.service.ITodoService
import jakarta.validation.Valid
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
@Validated
class TodoController(private val todoService: ITodoService) {

    @GetMapping
    fun getAllTodos(
        @RequestParam(defaultValue = "") filter: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): PageImpl<TodoResource> {
        val pageable = PageRequest.of(page, size)
        return todoService.findAllTodos(pageable, filter)
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long) = todoService.findTodoById(id)

    @PostMapping
    fun createTodo(@RequestBody @Valid resource: TodoResource) = todoService.createTodo(resource)

    @PutMapping("/{id}")
    fun updateTodo(@PathVariable id: Long, @RequestBody @Valid resource: TodoResource) = todoService.updateTodo(id, resource)

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long) = todoService.deleteTodoById(id)
}
