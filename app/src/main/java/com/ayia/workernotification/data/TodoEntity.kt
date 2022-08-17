package com.ayia.workernotification.data

import androidx.room.*
import com.ayia.workernotification.*
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.util.*


@Entity(tableName = TABLE_TODOS)
data class TodoEntity(
    @ColumnInfo(name = COLUMN_TODO_ID)
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = COLUMN_TODO_DATE) val date: Long = System.currentTimeMillis(),
    @ColumnInfo(name = COLUMN_TODO_TITLE) var title: String? = toDateTimeString(date),
    @ColumnInfo(name = COLUMN_TODO_DATE_UPDATED) var updated: Long? = date,
    @ColumnInfo(name = COLUMN_TODO_DONE) var done: Boolean = false,
    @ColumnInfo(name = COLUMN_TODO_DATE_DEADLINE) var deadline: Long? = null,
    @ColumnInfo(name = COLUMN_TODO_PRIORITY) var priority: Int? = null,
    @ColumnInfo(name = COLUMN_TODO_IS_REMINDER_ON) var isReminderOn: Boolean = false,

    ) {

    fun toTodo(): Todo {
        return Todo(
            id = id,
            date = date,
            title = title,
            updated = updated,
            isDone = done,
            deadline = deadline,
            priority = priority,
            isReminderOn = isReminderOn
        )
    }

}

