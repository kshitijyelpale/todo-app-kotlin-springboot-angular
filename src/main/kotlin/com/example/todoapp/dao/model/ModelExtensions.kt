package com.example.todoapp.dao.model

import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.dao.resource.TodoResource

fun Todo.toResource() = TodoResource(
    id = id,
    name = name,
    description = description,
    tasks = tasks.map { it.toResource() }.toMutableSet()
)

fun Task.toResource() = TaskResource(
    id = id,
    name = name,
    description = description,
    completed = completed,
    todoId = todo?.id
)

fun TodoResource.toModel() = Todo(
    name = name,
    description = description,
)

fun TaskResource.toModel() = Task(
    name = name,
    description = description,
    completed = completed,
    todo = null
)
