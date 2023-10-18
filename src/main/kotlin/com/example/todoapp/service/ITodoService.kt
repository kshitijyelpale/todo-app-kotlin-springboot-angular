package com.example.todoapp.service

import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.exception.ServiceException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

interface ITodoService {

    fun findAllTodos(pageable: PageRequest, filter: String): PageImpl<TodoResource>

    fun findTodoById(id: Long): TodoResource

    @Transactional
    @Throws(ServiceException::class)
    fun createTodo(resource: TodoResource): TodoResource

    @Transactional
    @Throws(ServiceException::class, NoSuchElementException::class)
    fun updateTodo(id: Long, resource: TodoResource): TodoResource

    @Transactional
    @Throws(ServiceException::class, NoSuchElementException::class)
    fun deleteTodoById(id: Long): Boolean
}
