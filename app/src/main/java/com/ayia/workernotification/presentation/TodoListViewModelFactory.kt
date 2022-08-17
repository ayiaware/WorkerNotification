package com.ayia.workernotification.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayia.workernotification.framework.TodoInteractors

@Suppress("UNCHECKED_CAST")
class TodoListViewModelFactory(
    val todoInteractors: TodoInteractors,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            return TodoListViewModel(
                 todoInteractors
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}