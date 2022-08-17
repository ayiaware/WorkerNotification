package com.ayia.workernotification.presentation

import androidx.lifecycle.*
import android.text.TextUtils

import androidx.lifecycle.LiveData
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.util.QUERY_KEY_TODO
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoInteractors

import timber.log.Timber


class MainViewModel(
    val todoInteractors: TodoInteractors,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val myTag: String = GLOBAL_TAG + " " + MainViewModel::class.java.simpleName

    private val defaultFilter = arrayOf("0", "")

    val searchQueryTodo = MutableLiveData(defaultFilter[1])

    val todos: LiveData<List<Todo>?> =
        savedStateHandle.getLiveData(QUERY_KEY_TODO, defaultFilter).switchMap { f ->

            val query = f[1]

            searchQueryTodo.value = query

            if (TextUtils.isEmpty(query)){
                todoInteractors.getTodos()
            }
            else
                todoInteractors.getTodos(nameSearchQuery = "%$query%")

        }

    fun setQuery(query: String) {

        val filter = arrayOf("0", query)

        Timber.tag(myTag).d("filter Query $query ")
        savedStateHandle[QUERY_KEY_TODO] = filter
    }



    

}