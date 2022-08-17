package com.ayia.workernotification.framework

import com.ayia.workernotification.data.TodoEntity
import com.ayia.workernotification.domain.Todo


fun Todo.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        date = date,
        title = title,
        updated = updated,
        done = isDone,
        deadline = deadline,
        isReminderOn = isReminderOn,
        priority = priority,
    )
}