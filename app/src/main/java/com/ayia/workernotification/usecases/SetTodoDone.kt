package com.ayia.workernotification.usecases

import com.ayia.workernotification.data.TodoRepository

class SetTodoDone(private val repository: TodoRepository) {
    suspend operator fun invoke(id: Int) = repository.setTodoDone(id)
}