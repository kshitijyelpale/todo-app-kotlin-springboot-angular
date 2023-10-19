package com.example.todoapp.dao.model

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq_generator")
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "task_seq_generator", sequenceName = "task_id_seq", allocationSize = 1)
    val id: Long = 0, // will be replaced by hibernate

    @Column(nullable = false)
    @NotBlank
    @Max(value = 50)
    var name: String,

    @Column(nullable = true)
    @Max(value = 500)
    var description: String? = null,

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    var todo: Todo?
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Task(id=$id, name='$name', description='$description', todo=$todo)"
    }
}
