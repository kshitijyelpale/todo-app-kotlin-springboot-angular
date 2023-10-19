package com.example.todoapp

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
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
import java.util.*

class CustomInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
	override fun initialize(applicationContext: ConfigurableApplicationContext) {}
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = [CustomInitializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class TodoAppApplicationTests {

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

	@Test
	fun contextLoads() {
	}

}
