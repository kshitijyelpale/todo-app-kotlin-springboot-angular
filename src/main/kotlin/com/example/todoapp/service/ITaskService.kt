package com.example.todoapp.service

import com.example.todoapp.dao.model.Task
import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import org.springframework.transaction.annotation.Transactional

interface ITaskService {

    fun findAllTasks(): List<TaskResource>

    fun findAllTasksByTodoId(id: Long): Set<Task>

    @Transactional
    @Throws(ServiceException::class)
    fun createTasks(resources: Set<TaskResource>, todo: Todo): Set<Task>

    @Transactional
    @Throws(NoSuchElementFoundException::class, ServiceException::class)
    fun updateTasks(resources: Set<TaskResource>): Set<Task>

    @Transactional
    @Throws(NoSuchElementFoundException::class, ServiceException::class)
    fun deleteTasksByTodoId(id: Long): Boolean
}
