package com.example.todoapp.dao.repository

import com.example.todoapp.dao.model.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo>
