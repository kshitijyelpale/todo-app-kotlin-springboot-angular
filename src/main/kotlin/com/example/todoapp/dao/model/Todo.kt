package com.example.todoapp.dao.model

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "todo")
data class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_seq_generator")
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "todo_seq_generator", sequenceName = "todo_id_seq", allocationSize = 1)
    val id: Long = 0, // will be replaced by hibernate

    @Column(nullable = false)
    @NotBlank
    @Max(value = 20)
    var name: String,

    @Column(nullable = true)
    @Max(value = 500)
    var description: String? = null,

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var tasks: MutableSet<Task> = hashSetOf()
): BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Todo

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (tasks != other.tasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + tasks.hashCode()

        return result
    }

    override fun toString(): String {
        return "Todo(id=$id, name='$name', description=$description, tasks=$tasks)"
    }
}
