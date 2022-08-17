package com.ayia.workernotification.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ayia.workernotification.*
import com.ayia.workernotification.util.*

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: TodoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoGetId(todo: TodoEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: List<TodoEntity>)

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Update
    suspend fun updateTodo(todo: List<TodoEntity>)

    @Query("select * from $TABLE_TODOS where $COLUMN_TODO_ID =:id limit 1")
    fun getTodoObs(id: Int): LiveData<TodoEntity?>

    @Query("select * from $TABLE_TODOS where $COLUMN_TODO_ID =:id limit 1")
    fun getTodo(id: Int): TodoEntity?

    @Query("select * from $TABLE_TODOS order by $COLUMN_TODO_DATE_UPDATED DESC")
    fun getActiveTodosObs(): LiveData<MutableList<TodoEntity>?>

    @Query("select * from $TABLE_TODOS where $COLUMN_TODO_TITLE LIKE :nameSearchQuery")
    fun getActiveTodosObs(nameSearchQuery: String): LiveData<MutableList<TodoEntity>?>

    //get observable trashed todos for a particular section in which parents(folders) are not trashed
    @Transaction
    @Query("select * from $TABLE_TODOS order by $COLUMN_TODO_DATE_UPDATED DESC")
    fun getTrashedTodosObs(): LiveData<MutableList<TodoEntity>?>

    @Delete
    suspend fun deleteTodo(todo: List<TodoEntity>)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("UPDATE $TABLE_TODOS SET $COLUMN_TODO_DONE = 1 WHERE $COLUMN_TODO_ID = :id")
    suspend fun setTodoDone(id: Int)


}