package com.ayia.workernotification.usecases

import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.data.TodoRepository


class UpdateTodo (private val repository: TodoRepository) {

    suspend operator fun invoke(todo: Todo) = repository.updateTodo(todo)

    suspend operator fun invoke(todos: List<Todo>) = repository.updateTodo(todos)

}