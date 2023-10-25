package com.example.todoapp

import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.model.toResource
import com.example.todoapp.dao.resource.TaskResource
import com.example.todoapp.dao.resource.TodoResource
import com.example.todoapp.exception.ServiceException
import com.example.todoapp.service.ITaskService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doThrow
import org.springframework.boot.test.mock.mockito.SpyBean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


class TodoFeatureTest : BaseFeatureTest() {

    @SpyBean
    override lateinit var taskService: ITaskService

    private val task1 = TaskResource(
        id = 0,
        name = "test task",
        description = "some description",
        completed = false
    )

    private val task2 = TaskResource(
        id = 0,
        name = "test task 2",
        description = "some description 2",
        completed = false
    )

    private val todo = TodoResource(
        id = 0,
        name = "test todo",
        description = "todo1 desc",
        tasks = mutableSetOf(task1, task2)
    )

    @BeforeAll
    fun setUp() {
        if (todoRepository.count() == 0L) {
            this.createTodos()
        }
    }

    @Test
    fun `should have todos and tasks created`() {
        assertTrue(taskRepository.count() >= 10)
        assertTrue(todoService.getTotalCountOfTodos() >= 10)
    }

    @Test
    fun `should find a created todo in setup`() {
        // when
        val todo1 = todoService.findTodoById(1);

        // then
        assertEquals(1, todo1.id)
        assertEquals("todo1", todo1.name)
        assertEquals("todo 1 description 1", todo1.description)
        assertEquals(1, todo1.tasks.size)
        assertEquals("task1", todo1.tasks.first().name)
        assertEquals("task 1 description 1", todo1.tasks.first().description)
    }

    @Test
    fun `should create new todo`() {
        // given
        val existingTodoCount = todoRepository.count()
        // when
        val todo = todoService.createTodo(todo)

        // then
        assertTrue(todo.id > 0)
        assertEquals("test todo", todo.name)
        assertEquals("todo1 desc", todo.description)
        assertEquals(2, todo.tasks.size)
        assertEquals("test task", todo.tasks.first().name)
        assertEquals("some description", todo.tasks.first().description)
        assertEquals(false, todo.tasks.first().completed)
    }

    @Test
    fun `should delete a todo`() {
        // given
        val existingTodoCount = todoRepository.count()
        val existingTaskCount = taskRepository.count()
        // when
        todoService.deleteTodoById(5)

        // then
        assertEquals(existingTodoCount - 1, todoRepository.count())
        assertEquals(existingTaskCount - 1, taskRepository.count())
    }

    @Test
    fun `should update a todo`() {
        // given
        val todo = todoRepository.findById(4).get()
        val todoResource = todo.toResource().copy(
            name = "new todo name",
            description = "new todo description"
        )
        todoResource.tasks.add(
            task2.copy(
                description = "something different"
            )
        )

        // when
        val updatedTodo = todoService.updateTodo(4, todoResource)

        // then
        assertEquals(todo.id, updatedTodo.id)
        assertEquals(todoResource.name, updatedTodo.name)
        assertEquals(todoResource.description, updatedTodo.description)
        assertEquals(2, updatedTodo.tasks.size)
        assertEquals(
            setOf("task 4 description 4", "something different"),
            updatedTodo.tasks.map { it.description }.toSet()
        )
    }

    @Test
    fun `should update a todo with no tasks`() {
        // given
        val todo = todoRepository.findById(3).get()
        val todoResource = todo.toResource().copy(
            name = "new todo name",
            description = "new todo description",
            tasks = mutableSetOf()
        )

        // when
        val updatedTodo = todoService.updateTodo(2, todoResource)

        // then
        assertEquals("new todo name", updatedTodo.name)
        assertEquals("new todo description", updatedTodo.description)
        assertEquals(0, updatedTodo.tasks.size)
    }

    @Test
    fun `should rollback an update of todo if update of task fails`() {
        // given
        val todo = todoRepository.findById(7).get()
        val todoResource = todo.toResource().copy(
            name = "update todo 7",
            description = "new todo description"
        )
        todoResource.tasks.add(
            task2.copy(
                description = "something different"
            )
        )

        doThrow(ServiceException("Something is wrong")).`when`(taskService).updateTasks(todoResource.tasks, todo)

        // when/then
        assertThrows<ServiceException> { todoService.updateTodo(7, todoResource) }
        val updatedTodo = todoRepository.findById(7).get()
        assertEquals(todo, updatedTodo)
    }

    @Test
    fun `should update todo in concurrent environment`() {
        // given

        val todo = todoRepository.findById(7).get()
        val todoResource = todo.toResource().copy(
            name = "update todo 7",
            description = "new todo description"
        )

        val numberOfParallelExecutions = 3
        val syncPointTasksFinished = CountDownLatch(numberOfParallelExecutions)
        val executor = Executors.newFixedThreadPool(numberOfParallelExecutions)
        var updatedTodo: Todo? = null

        // when
        for (i in 1 .. numberOfParallelExecutions) {
            executor.execute {
                try {
                    todoResource.tasks.add(
                        task2.copy(
                            name = "concurrent execution $i",
                            description = "concurrent execution $i"
                        )
                    )
                    updatedTodo = todoService.updateTodo(7, todoResource)
                }
                finally {
                    syncPointTasksFinished.countDown()
                }
            }
        }
        syncPointTasksFinished.await()
        executor.shutdown()

        // then
        assertEquals(4, updatedTodo?.tasks?.size)
    }
}
