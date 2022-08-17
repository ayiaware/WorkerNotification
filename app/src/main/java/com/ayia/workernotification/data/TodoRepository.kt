package com.ayia.workernotification.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.toEntity
import com.ayia.workernotification.notification.Scheduler
import com.ayia.workernotification.util.GLOBAL_TAG

import timber.log.Timber

class TodoRepository(private val dao: TodoDao) : TodoDataSource {

    private val myTag: String = GLOBAL_TAG + " " + TodoRepository::class.java.simpleName

    override suspend fun insertTodo(todo: Todo) {

        Timber.tag(myTag).d("insertTodo ${todo.deadline} ")

        val todoId = dao.insertTodoGetId(todo.toEntity())

        if (todo.deadline != null) {

            Timber.tag(myTag).d("todo.deadline != null true")

            todo.id = todoId.toInt()
            Scheduler(todo).schedule()
        }

    }

    override suspend fun insertTodo(todos: List<Todo>) =
        dao.insertTodo(todos.map { it.toEntity() })

    override fun getTodo(id: Int): Todo? {
        return dao.getTodo(id)?.toTodo()
    }

    override fun getTodoObs(id: Int): LiveData<Todo?> {
        return dao.getTodoObs(id).map { it?.toTodo() }
    }

    override fun getTodos(): LiveData<List<Todo>?> {
        return dao.getActiveTodosObs().map {
            it?.map { todoEntity ->
                todoEntity.toTodo()
            }
        }

    }


    override fun getTodos(
        nameSearchQuery: String
    ): LiveData<List<Todo>?> {
        return dao.getActiveTodosObs(nameSearchQuery).map {
            it?.map { todoEntity ->
                todoEntity.toTodo()
            }
        }
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo.toEntity())

    }

    override suspend fun deleteTodo(todos: List<Todo>) {

        dao.deleteTodo(todos.map {
            it.toEntity()
        })


    }


    override suspend fun updateTodo(todo: Todo) {

        val old: Todo? = dao.getTodo(todo.id)?.toTodo()

        dao.updateTodo(todo.toEntity())

        old?.let {
            if (it.isReminderOn && !todo.isReminderOn) {
                Scheduler(todo).removeReminder()
            } else if ((!it.isReminderOn && todo.isReminderOn) || (it.deadline != todo.deadline)) {
                Scheduler(todo).schedule()
            }

        }

    }


    override suspend fun updateTodo(todos: List<Todo>) {
        dao.updateTodo(todos.map {
            it.toEntity()
        })
    }

    override suspend fun setTodoDone(id: Int) {
        dao.setTodoDone(id)
    }




}