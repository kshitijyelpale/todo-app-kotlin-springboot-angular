package com.example.todoapp.dao.resource

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

data class TodoResource(
    val id: Long,
    @NotBlank
    @Max(value = 100)
    val name: String,
    @Max(value = 100)
    val description: String,
    var tasks: MutableSet<TaskResource> = hashSetOf()
)
