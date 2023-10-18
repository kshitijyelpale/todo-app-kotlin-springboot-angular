package com.example.todoapp.service

import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.model.toModel
import com.example.todoapp.dao.model.toResource
import com.example.todoapp.dao.repository.TaskRepository
import com.example.todoapp.dao.repository.TodoRepository
import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import mu.KotlinLogging
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
@Transactional
internal class TodoService(
    private val todoRepository: TodoRepository,
    private val taskRepository: TaskRepository,
    private val taskService: ITaskService,
    private val transactionTemplate: TransactionTemplate
) : ITodoService {

    private val accessLock = ReentrantLock()

    private val logger = KotlinLogging.logger {}

    override fun findAllTodos(pageable: PageRequest, filter: String): PageImpl<TodoResource> {
        val todoPage = todoRepository.findAll(filterBy(filter), pageable)
        val content = todoPage.content.map { it.toResource() }
        return PageImpl(content, todoPage.pageable, todoPage.totalElements)
    }

    @Throws(NoSuchElementFoundException::class)
    override fun findTodoById(id: Long): TodoResource {
        val todo = fetchTodoById(id)
        todo.tasks = taskRepository.findAllByTodoId(todo.id).toMutableSet()
        return todo.toResource()
    }

    override fun createTodo(resource: TodoResource): TodoResource {
        val todo = resource.toModel()
        val save = todoRepository.save(todo)
        val todoResource = save.toResource()
        if (resource.tasks.isNotEmpty()) {
            todoResource.tasks = taskService.createTasks(resource.tasks, save).toMutableSet()
        }

        return todoResource
    }

    override fun updateTodo(id: Long, resource: TodoResource): TodoResource {
        val message = "Todo with id $id update failed"

        return accessLock.withLock {
            val updatedTodo = transactionTemplate.execute {
                try {
                    val existingTodo = fetchTodoById(id)
                    existingTodo.name = resource.name
                    existingTodo.description = resource.description
                    val savedTodo = todoRepository.save(existingTodo)
                    if (resource.tasks.isNotEmpty()) {
                        taskService.updateTasks(resource.tasks)
                    }

                    savedTodo.toResource()
                } catch (e: Exception) {
                    logger.error(message, e)
                    throw ServiceException(message)
                }
            }
            updatedTodo ?: throw ServiceException(message)
        }
    }

    override fun deleteTodoById(id: Long): Boolean {
        return try {
            taskRepository.deleteAllByTodoId(id)
            todoRepository.deleteById(id)
            true
        } catch (e: Exception) {
            logger.error { "Deletion of todo having id $id failed: ${e.message}" }
            false
        }
    }

    private fun fetchTodoById(id: Long): Todo {
        return todoRepository.findByIdOrNull(id) ?: throw NoSuchElementFoundException("Todo $id not found")
    }

    private fun filterBy(keyword: String): Specification<Todo> {
        return Specification { root, _, cb ->
            if (keyword.isBlank()) {
                // If the filter is blank, return a null predicate to select all records.
                null
            } else {
                val keywordExpression = "%$keyword%"
                val namePredicate = cb.like(root.get("name"), keywordExpression)
                val descriptionPredicate = cb.like(root.get("description"), keywordExpression)
                cb.or(namePredicate, descriptionPredicate)
            }
        }
    }
}
