import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.flywaydb.flyway") version "9.8.1"
	id("com.github.node-gradle.node") version "7.0.0"
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
//	id("org.hibernate.orm") version "6.2.9.Final"
	id("org.graalvm.buildtools.native") version "0.9.27"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    kotlin("plugin.jpa") version "1.9.0"
//	kotlin("kapt") version "1.9.0"
}

val springBootVersion = "3.1.4"

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_20
}

repositories {
	mavenCentral()
	maven("https://repo.spring.io/milestone")
//	jcenter() // deprecated and readonly as of Oct. 31st 2022 !!
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
//	kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-hateoas:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")
	implementation("org.hibernate:hibernate-validator:8.0.1.Final")
	implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.github.microutils:kotlin-logging:2.1.23")
//	implementation("au.com.console:kotlin-jpa-specification-dsl:2.0.0")
//	implementation("org.springframework.data:spring-data-rest-hal-explorer")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "20"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
    project.property("snippetsDir")?.let { outputs.dir(it) }
}

tasks.asciidoctor {
    project.property("snippetsDir")?.let { inputs.dir(it) }
//	dependsOn(test)
}

flyway {
	user = "todo"
	password = "todo"
	url = "jdbc:postgresql://localhost:5432/postgres"
}

//hibernate {
//	enhancement {
//		enableAssociationManagement.set(true)
//	}
//}

