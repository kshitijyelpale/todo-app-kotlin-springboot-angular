package com.example.todoapp.dao.resource

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

data class TaskResource(
    val id: Long,
    @NotBlank
    @Max(value = 50)
    val name: String,
    @Max(value = 500)
    val description: String? = null,
    val todoId: Long? = null,
)
