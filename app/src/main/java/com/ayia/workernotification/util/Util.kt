package com.ayia.workernotification.util

import android.text.SpannableStringBuilder
import androidx.core.text.bold
import com.ayia.workernotification.domain.Todo

fun getTextToShare(todo: Todo, strings: List<String> = listOf()): String {

    val s = SpannableStringBuilder()

    s.bold {
        append("${strings[0]} : ${todo.title}")
    }
    if (todo.deadline != null) {
        s.append("\n")
        s.append("@ ${toDateTimeString(todo.deadline!!)}")
    }
    s.append("\n")
    s.append(if (todo.isDone) "${strings[1]} [\" + \"\\u2713\" + \"]" else "${strings[2]} [  ]")

    return s.toString()
}