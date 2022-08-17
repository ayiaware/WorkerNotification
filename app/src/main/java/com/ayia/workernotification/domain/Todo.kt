package com.ayia.workernotification.domain

import com.ayia.workernotification.util.toDateTimeString

data class Todo(
    var id: Int = 0,
    var folderId: Int = 0,
    val date: Long = System.currentTimeMillis(),
    var title: String? = toDateTimeString(date),
    var updated: Long? = date,
    var isDone: Boolean = false,
    var deadline: Long? = null,
    var isReminderOn : Boolean = false,
    var priority: Int? = null
)



