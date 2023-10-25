package com.example.todoapp

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TodoControllerTest : BaseFeatureTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val api = "/todos"

    @BeforeEach
    fun setUp() {
        this.createTodos()
    }

    @Test
    fun `should return all resources correctly fetching all todos`() {
        mockMvc.perform(get(api))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.todoResources.size()").value(10))
            .andExpect(jsonPath("$._embedded.todoResources[4].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[4].name").value("todo5"))
            .andExpect(jsonPath("$._embedded.todoResources[4].description").value("todo 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[4].tasks[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[4].tasks[0].name").value("task5"))
            .andExpect(jsonPath("$._embedded.todoResources[4].tasks[0].description").value("task 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[4].tasks[0].completed").value(false))
            .andExpect(jsonPath("$._embedded.todoResources[4].tasks[0].todoId").value(5))
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.next").doesNotExist())
            .andExpect(jsonPath("$._links.prev").doesNotExist())
    }

    @Test
    fun `should return resources with pagination parameters`() {
        mockMvc.perform(get("$api?page=1&size=3"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.todoResources.size()").value(3))
            .andExpect(jsonPath("$._embedded.todoResources[1].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[1].name").value("todo5"))
            .andExpect(jsonPath("$._embedded.todoResources[1].description").value("todo 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[1].tasks[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[1].tasks[0].name").value("task5"))
            .andExpect(jsonPath("$._embedded.todoResources[1].tasks[0].description").value("task 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[1].tasks[0].completed").value(false))
            .andExpect(jsonPath("$._embedded.todoResources[1].tasks[0].todoId").value(5))
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.next").exists())
            .andExpect(jsonPath("$._links.prev").exists())
    }

    @Test
    fun `should return resources with filtering todo name`() {
        mockMvc.perform(get("$api?filter=do5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.todoResources.size()").value(1))
            .andExpect(jsonPath("$._embedded.todoResources[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[0].name").value("todo5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].description").value("todo 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].name").value("task5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].description").value("task 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].completed").value(false))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].todoId").value(5))
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.next").doesNotExist())
            .andExpect(jsonPath("$._links.prev").doesNotExist())
    }

    @Test
    fun `should return resources with filtering todo description`() {
        mockMvc.perform(get("$api?filter=5 desc"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.todoResources.size()").value(1))
            .andExpect(jsonPath("$._embedded.todoResources[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[0].name").value("todo5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].description").value("todo 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].id").value(5))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].name").value("task5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].description").value("task 5 description 5"))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].completed").value(false))
            .andExpect(jsonPath("$._embedded.todoResources[0].tasks[0].todoId").value(5))
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$._links.self").exists())
            .andExpect(jsonPath("$._links.next").doesNotExist())
            .andExpect(jsonPath("$._links.prev").doesNotExist())
    }
}
