package com.example.todoapp.service

import com.example.todoapp.dao.model.Task
import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.model.toModel
import com.example.todoapp.dao.repository.TodoRepository
import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
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
    private val taskService: ITaskService,
    private val transactionTemplate: TransactionTemplate,
    private val todoResourceAssembler: TodoResourceAssembler
) : ITodoService {

    private val accessLock = ReentrantLock()

    private val logger = KotlinLogging.logger {}

    override fun findAllTodos(pageable: PageRequest, filter: String): CollectionModel<EntityModel<TodoResource>> {
        val todoPage = todoRepository.findAll(filterBy(filter), pageable)
        return todoResourceAssembler.decorateWithLinks(todoPage, filter)
    }

    @Throws(NoSuchElementFoundException::class)
    override fun findTodoById(id: Long): Todo {
        val todo = fetchTodoById(id)
        todo.tasks = taskService.findAllTasksByTodoId(id) as MutableSet<Task>
        return todo
    }

    override fun createTodo(resource: TodoResource): Todo {
        val todo = resource.toModel()
        val save = todoRepository.save(todo)
        if (resource.tasks.isNotEmpty()) {
            save.tasks = taskService.createTasks(resource.tasks, save) as MutableSet<Task>
        }

        return save
    }

    override fun updateTodo(id: Long, resource: TodoResource): Todo {
        val message = "Todo with id $id update failed"

        return accessLock.withLock {
            val updatedTodo = transactionTemplate.execute {
                try {
                    logger.debug { "Updating todo $id" }
                    val existingTodo = fetchTodoById(id)
                    if (resource.name.isNotBlank()) {
                        existingTodo.name = resource.name
                    }
                    existingTodo.description = resource.description?.ifEmpty { null }
                    val savedTodo = todoRepository.save(existingTodo)
                    if (resource.tasks.isNotEmpty()) {
                        savedTodo.tasks = taskService.updateTasks(resource.tasks) as MutableSet<Task>
                    }

                    savedTodo
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
            logger.debug { "Deleting todo $id" }
            taskService.deleteTasksByTodoId(id)
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
