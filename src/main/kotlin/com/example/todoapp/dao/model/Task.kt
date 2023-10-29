package com.example.todoapp.dao.model

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import java.sql.Timestamp

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq_generator")
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "task_seq_generator", sequenceName = "task_id_seq", allocationSize = 1)
    val id: Long = 0, // will be replaced by hibernate
//    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    @NotBlank
    @Max(value = 50)
    var name: String,

    @Column
    @Max(value = 500)
    var description: String? = null,

    @Column(nullable = false)
    var completed: Boolean = false,

    @Column(name = "due_date")
    var dueDate: Timestamp? = null,

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    var todo: Todo?
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (completed != other.completed) return false
        if (dueDate != other.dueDate) return false
        if (todo != other.todo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + completed.hashCode()
        result = 31 * result + (dueDate?.hashCode() ?: 0)
        result = 31 * result + (todo?.hashCode() ?: 0)

        return result
    }

    override fun toString(): String {
        return "Task(id=$id, name='$name', description=$description, completed=$completed, dueDate=$dueDate, todo=$todo)"
    }
}
