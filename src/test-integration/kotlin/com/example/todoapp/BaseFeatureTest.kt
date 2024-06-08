package com.example.todoapp

import com.example.todoapp.dao.model.Task
import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.repository.TaskRepository
import com.example.todoapp.dao.repository.TodoRepository
import com.example.todoapp.service.ITaskService
import com.example.todoapp.service.ITodoService
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

class CustomInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {}
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = [CustomInitializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
abstract class BaseFeatureTest {

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("test_db")
            withUsername("test")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            container.start()
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }
    }

    @Autowired
    protected lateinit var todoService: ITodoService

    @Autowired
    protected lateinit var taskService: ITaskService

    @Autowired
    protected lateinit var taskRepository: TaskRepository

    @Autowired
    protected lateinit var todoRepository: TodoRepository

    protected fun createTodos() {
        var todos = mutableListOf<Todo>()
        for (i in 1..10) {
            todos.add(
                Todo(
                    name = "todo$i",
                    description = "todo $i description $i",
                )
            )
        }
        todos = todoRepository.saveAllAndFlush(todos)

        val tasks = mutableListOf<Task>()
        for (i in 1..10) {
            tasks.add(
                Task(
                    name = "task$i",
                    description = "task $i description $i",
                    completed = false,
                    todo = todos[i - 1]
                )
            )
        }
        taskRepository.saveAllAndFlush(tasks)
    }
}
