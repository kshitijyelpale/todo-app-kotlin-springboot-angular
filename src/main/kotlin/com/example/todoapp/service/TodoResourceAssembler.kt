package com.example.todoapp.service

import com.example.todoapp.controller.TodoController
import com.example.todoapp.dao.model.Todo
import com.example.todoapp.dao.model.toResource
import com.example.todoapp.dao.resource.TodoResource
import org.springframework.data.domain.Page
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder

@Component
class TodoResourceAssembler : RepresentationModelAssembler<Todo, EntityModel<TodoResource>> {

    override fun toModel(entity: Todo): EntityModel<TodoResource> {
        val todoResource = entity.toResource()

        val selfLink = WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(TodoController::class.java).getTodoById(entity.id)
        ).withSelfRel()

        return EntityModel.of(todoResource, selfLink)
    }

    override fun toCollectionModel(entities: MutableIterable<Todo>): CollectionModel<EntityModel<TodoResource>> {
        val todoResources = entities.map { toModel(it) }
        return CollectionModel.of(todoResources)
    }

    fun decorateWithLinks(todoPage: Page<Todo>, filter: String): CollectionModel<EntityModel<TodoResource>> {

        val collectionModel = toCollectionModel(todoPage.content)

        collectionModel.add(
            createLink(filter, 0, todoPage.size, "first", "First Page"),
            createLink(filter, todoPage.totalPages - 1, todoPage.size, "last", "Last Page")
        )

        if (todoPage.hasPrevious()) {
            collectionModel.add(
                createLink(filter, todoPage.number - 1, todoPage.size, "prev", "Previous Page")
            )
        }

        if (todoPage.hasNext()) {
            collectionModel.add(
                createLink(filter, todoPage.number + 1, todoPage.size, "next", "Next Page")
            )
        }

        collectionModel.add(
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TodoController::class.java).getAllTodos(filter, todoPage.number, todoPage.size)
            ).withSelfRel()
        )

        return collectionModel
    }

    private fun createLink(filter: String, page: Int, size: Int, rel: String, title: String = ""): Link {
        val baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString()
        return Link.of(UriComponentsBuilder.fromUriString(baseUri)
            .replaceQueryParam("filter", filter)
            .replaceQueryParam("page", page)
            .replaceQueryParam("size", size)
            .toUriString(), rel)
            .withRel(rel).withTitle(title)
    }
}
