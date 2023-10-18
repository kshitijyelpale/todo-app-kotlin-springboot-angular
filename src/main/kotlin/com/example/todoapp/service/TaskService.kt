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
        logger.info { "retrieving all tasks" }

        return taskRepository.findAll().map { it.toResource() }
    }

    @Throws(NoSuchElementFoundException::class)
    override fun findTaskById(id: Long): TaskResource {
        logger.info { "retrieving task $id" }

        return fetchTaskById(id).toResource()
    }

    override fun createTasks(resources: Set<TaskResource>, todo: Todo): Set<TaskResource> {
        try {
            val tasks = resources.map { it.toModel() }
            tasks.forEach { it.todo = todo }

            val list = taskRepository.saveAllAndFlush(tasks)
            return list.map { it.toResource() }.toMutableSet()
        } catch (e: Exception) {
            val message = "Task creation failed: ${e.message}"
            logger.error { message }
            throw ServiceException(message)
        }
    }

    override fun updateTasks(resources: Set<TaskResource>): Set<TaskResource> {
        val message = "Tasks update failed"

        return accessLock.withLock {
            val tasks = transactionTemplate.execute {
                try {
                    resources.map {
                        val existingTask = fetchTaskById(it.id)
                        existingTask.name = it.name
                        existingTask.description = it.description
                        taskRepository.save(existingTask)
                    }
                } catch (e: Exception) {
                    logger.error(message, e)
                    throw ServiceException(message)
                }
            }
            tasks?.map { it.toResource() }?.toMutableSet() ?: throw ServiceException(message)
        }
    }

    override fun deleteTaskByTaskId(id: Long): Boolean {
        return try {
            taskRepository.deleteById(id)
            true
        } catch (e: Exception) {
            val message = "Deletion of task having id $id failed: ${e.message}"
            logger.error { message }
            throw ServiceException(message)
        }
    }

    override fun deleteTaskByTodoId(id: Long): Boolean {
        return try {
            taskRepository.deleteAllByTodoId(id)
            true
        } catch (e: Exception) {
            val message = "Deletion of tasks having todo id $id failed: ${e.message}"
            logger.error { message }
            throw ServiceException(message)
        }
    }

    private fun fetchTaskById(id: Long): Task {
        return taskRepository.findByIdOrNull(id) ?: throw NoSuchElementFoundException("Task $id not found")
    }
}
