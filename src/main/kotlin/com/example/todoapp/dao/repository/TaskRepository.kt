package com.example.todoapp.dao.repository

import com.example.todoapp.dao.model.Task
import com.example.todoapp.dao.model.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    fun findAllByTodoId(todo: Long): List<Task>

    fun deleteAllByTodoId(todoId: Long)
}
