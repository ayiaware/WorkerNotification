package com.ayia.workernotification.usecases


import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.data.TodoRepository

class DeleteTodo(private val repository: TodoRepository) {

    suspend operator fun invoke(todo: Todo) = repository.deleteTodo(todo = todo)

    suspend operator fun invoke(todos: List<Todo>) = repository.deleteTodo(todos = todos)

}