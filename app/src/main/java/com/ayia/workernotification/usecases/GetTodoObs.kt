package com.ayia.workernotification.usecases

import com.ayia.workernotification.data.TodoRepository

class GetTodoObs (private val repository: TodoRepository){

    operator fun invoke(id : Int) = repository.getTodoObs(id)

}