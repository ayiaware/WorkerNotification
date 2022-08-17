package com.ayia.workernotification.framework

import android.app.Application
import com.ayia.workernotification.BuildConfig

import com.ayia.workernotification.data.AppDatabase
import com.ayia.workernotification.data.TodoRepository
import com.ayia.workernotification.usecases.*
import timber.log.Timber


class TodoApp : Application() {


    companion object {

        @get:Synchronized
        lateinit var instance: TodoApp
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


    }


    private fun getDatabase(): AppDatabase {
        return AppDatabase.getDatabase(this)
    }

    private fun getTodoRepository() = TodoRepository(getDatabase().todoDao())

    fun getTodoInteractions() = TodoInteractors(
        insertTodo = InsertTodo(getTodoRepository()),
        getTodo = GetTodo(getTodoRepository()),
        getTodoObs = GetTodoObs(getTodoRepository()),
        getTodos = GetTodos(getTodoRepository()),
        deleteTodo = DeleteTodo(getTodoRepository()),
        updateTodo = UpdateTodo(getTodoRepository()),
        setTodoDone = SetTodoDone(getTodoRepository())
    )



}