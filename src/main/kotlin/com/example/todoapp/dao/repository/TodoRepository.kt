package com.example.todoapp.dao.repository

import com.example.todoapp.dao.model.Todo
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

//    @Query("SELECT DISTINCT t FROM Todo t LEFT JOIN FETCH t.tasks")
//    fun findAllTodosWithTasks(): List<Todo>

//    @EntityGraph(attributePaths = ["tasks"])
//    override fun findAll(): MutableList<Todo>
}
