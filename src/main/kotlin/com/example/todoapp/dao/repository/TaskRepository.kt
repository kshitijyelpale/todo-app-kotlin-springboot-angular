package com.example.todoapp.dao.repository

import com.example.todoapp.dao.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface TaskRepository : JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    fun findAllByTodoId(todo: Long): List<Task>

    fun findByIdAndTodoId(id: Long, todoId: Long): Task?

    fun deleteAllByTodoId(todoId: Long)

    fun findAllByDueDateBefore(date: Timestamp): List<Task>
}
