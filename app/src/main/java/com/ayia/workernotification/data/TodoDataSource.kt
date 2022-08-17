package com.ayia.workernotification.data

import androidx.lifecycle.LiveData
import com.ayia.workernotification.domain.Todo

interface TodoDataSource {

    suspend fun insertTodo(todo: Todo)

    suspend fun insertTodo(todos: List<Todo>)

    fun getTodo(id: Int) : Todo?

    fun getTodoObs(id: Int) :  LiveData<Todo?>

    fun getTodos() : LiveData<List<Todo>?>

    fun getTodos(nameSearchQuery : String) : LiveData<List<Todo>?>

    suspend fun deleteTodo (todo: Todo)

    suspend fun deleteTodo (todos: List<Todo>)

    suspend fun updateTodo (todo: Todo)

    suspend fun updateTodo (todos: List<Todo>)

    suspend fun setTodoDone (id: Int)
}