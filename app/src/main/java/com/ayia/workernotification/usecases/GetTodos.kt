package com.ayia.workernotification.usecases

import com.ayia.workernotification.data.TodoRepository

class GetTodos(private val repository: TodoRepository) {

    operator fun invoke() = repository.getTodos()

    operator fun invoke(nameSearchQuery: String) =
        repository.getTodos(nameSearchQuery = nameSearchQuery)
}