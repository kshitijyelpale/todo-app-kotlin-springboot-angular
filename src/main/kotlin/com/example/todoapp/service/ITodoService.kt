package com.example.todoapp.service

import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.exception.ServiceException
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.transaction.annotation.Transactional

interface ITodoService {

    fun findAllTodos(pageable: PageRequest, filter: String): CollectionModel<EntityModel<TodoResource>>

    fun findTodoById(id: Long): Todo

    @Transactional
    @Throws(ServiceException::class)
    fun createTodo(resource: TodoResource): Todo

    @Transactional
    @Throws(ServiceException::class, NoSuchElementException::class)
    fun updateTodo(id: Long, resource: TodoResource): Todo

    fun getTotalCountOfTodos(): Long

    @Transactional
    @Throws(ServiceException::class, NoSuchElementException::class)
    fun deleteTodoById(id: Long): Boolean

    fun getDueDatedTodos(): List<TodoResource>
}
