package com.example.todoapp.service

import com.example.todoapp.dao.model.Task
import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.model.toModel
import com.example.todoapp.dao.model.toResource
import com.example.todoapp.dao.repository.TaskRepository
import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
@Transactional
internal class TaskService(
    private val taskRepository: TaskRepository,
    private val transactionTemplate: TransactionTemplate
) : ITaskService {

    private val accessLock = ReentrantLock()

    private val logger = KotlinLogging.logger {}

    override fun findAllTasks(): List<TaskResource> {
        logger.debug { "Retrieving all tasks" }

        return taskRepository.findAll().map { it.toResource() }
    }

    override fun findAllTasksByTodoId(id: Long): Set<Task> {
        logger.debug { "Retrieving tasks for todo $id" }

        return taskRepository.findAllByTodoId(id).toSet()
    }

    override fun createTasks(resources: Set<TaskResource>, todo: Todo): Set<Task> {
        try {
            logger.debug { "Creating tasks for todo ${todo.id}" }
            val tasks = resources.map { it.toModel() }
            tasks.forEach { it.todo = todo }

            return taskRepository.saveAllAndFlush(tasks).toSet()
        } catch (e: Exception) {
            val message = "Tasks creation failed: ${e.message}"
            logger.error { message }
            throw ServiceException(message)
        }
    }

    override fun updateTasks(resources: Set<TaskResource>, todo: Todo): Set<Task> {
        val message = "Tasks update failed"

        return accessLock.withLock {
            val tasks = transactionTemplate.execute {
                try {
                    logger.debug { "Updating tasks" }
                    val updatedTasks = mutableSetOf<Task>()
                    val newTasks = resources.filter { it.id == 0L }
                    if (newTasks.isNotEmpty()) {
                        updatedTasks.addAll(createTasks(newTasks.toSet(), todo))
                    }
                    resources.filter { it.id > 0 }.map {
                        val existingTask = fetchTaskById(it.id)
                        if (it.name.isNotBlank()) {
                            existingTask.name = it.name
                        }

                        existingTask.description = it.description?.ifEmpty { null }
                        updatedTasks.add(taskRepository.save(existingTask))
                    }
                    updatedTasks
                } catch (e: Exception) {
                    logger.error(message, e)
                    throw ServiceException(message)
                }
            }
            tasks?.toSet() ?: throw ServiceException(message)
        }
    }

    override fun updateTaskStatus(todoId: Long, taskId: Long, completedStatus: Boolean): Boolean {
        return try {
            val task = taskRepository.findByIdAndTodoId(taskId, todoId)
            task.completed = completedStatus
            taskRepository.save(task)
            true
        } catch (e: Exception) {
            val message = """
                Task completed status update failed: taskId: $taskId, todoId: $todoId, status: $completedStatus - 
                ${e.message}
            """.trimIndent()

            logger.error { message }
            false
        }
    }

    override fun deleteTasksByTodoId(id: Long): Boolean {
        return try {
            logger.debug { "Deleting tasks for todo $id" }
            taskRepository.deleteAllByTodoId(id)
            true
        } catch (e: Exception) {
            val message = "Deletion of task having id $id failed: ${e.message}"
            logger.error { message }
            throw ServiceException(message)
        }
    }

    private fun fetchTaskById(id: Long): Task {
        return taskRepository.findByIdOrNull(id) ?: throw NoSuchElementFoundException("Task $id not found")
    }
}
