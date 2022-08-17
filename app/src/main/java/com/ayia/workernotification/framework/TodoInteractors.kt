package com.ayia.workernotification.framework

import com.ayia.workernotification.usecases.*

data class TodoInteractors (
    val insertTodo: InsertTodo,
    val getTodos: GetTodos,
    val getTodo: GetTodo,
    val getTodoObs: GetTodoObs,
    val deleteTodo: DeleteTodo,
    val updateTodo: UpdateTodo,
    val setTodoDone: SetTodoDone
)