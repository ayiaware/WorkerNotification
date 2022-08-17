package com.ayia.workernotification.usecases

import com.ayia.workernotification.data.TodoRepository


class GetTodo (private val repository: TodoRepository){

    operator fun invoke(id : Int) = repository.getTodo(id)

}