package com.example.todoapp.dao.resource

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

data class TodoResource(
    val id: Long,
    @NotBlank
    @Max(value = 20)
    val name: String,
    @Max(value = 100)
    val description: String? = null,
    var tasks: MutableSet<TaskResource> = hashSetOf()
)
