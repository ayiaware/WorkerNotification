package com.ayia.workernotification.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayia.workernotification.framework.TodoInteractors


class TodoFormViewModelFactory(
    val application: Application,
    val todoInteractors: TodoInteractors,
    private val todoId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TodoFormViewModel::class.java)) {
            return TodoFormViewModel(
                application = application,
                interactors = todoInteractors,
                id = todoId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}