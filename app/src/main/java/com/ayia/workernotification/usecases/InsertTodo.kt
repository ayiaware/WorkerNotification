package com.ayia.workernotification.usecases


import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.data.TodoRepository

class InsertTodo(private val repository: TodoRepository) {

    suspend operator fun invoke(todo: Todo) = repository.insertTodo(todo)

    suspend operator fun invoke(todos: List<Todo>) = repository.insertTodo(todos)

}