package com.ayia.workernotification.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.lifecycle.LiveData
import com.ayia.workernotification.*
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoInteractors
import com.ayia.workernotification.util.GLOBAL_TAG

class TodoListViewModel(
    private val repository: TodoInteractors
) : ViewModel() {

    private val _noListLabel = MutableLiveData(
            R.string.msg_empty_todo_list
    )

    val noListLabel: LiveData<Int> = _noListLabel

    private val _noListDrawable = MutableLiveData(R.drawable.ic_todo_24)

    val noListDrawable: LiveData<Int> = _noListDrawable

    fun deleteTodos(todos: List<Todo>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo.invoke(todos = todos)
    }

    fun onCheckBoxClicked(todo: Todo, checked: Boolean) {

        todo.isDone = checked
        viewModelScope.launch {
            repository.updateTodo.invoke(listOf(todo))
        }

    }

}