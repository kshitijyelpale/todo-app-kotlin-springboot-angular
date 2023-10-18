package com.example.todoapp.service

import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import org.springframework.transaction.annotation.Transactional

interface ITaskService {

    fun findAllTasks(): List<TaskResource>

    @Throws(NoSuchElementFoundException::class)
    fun findTaskById(id: Long): TaskResource

    @Transactional
    @Throws(ServiceException::class)
    fun createTasks(resources: Set<TaskResource>, todo: Todo): Set<TaskResource>

    @Transactional
    @Throws(NoSuchElementFoundException::class, ServiceException::class)
    fun updateTasks(resources: Set<TaskResource>): Set<TaskResource>

    @Transactional
    @Throws(NoSuchElementFoundException::class, ServiceException::class)
    fun deleteTaskByTaskId(id: Long): Boolean

    @Transactional
    @Throws(NoSuchElementFoundException::class, ServiceException::class)
    fun deleteTaskByTodoId(id: Long): Boolean
}
