package com.ayia.workernotification.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.util.NOTIFICATION_ID
import com.ayia.workernotification.framework.TodoApp
import com.ayia.workernotification.framework.TodoInteractors
import kotlinx.coroutines.*
import timber.log.Timber


class BroadcastReceiver : BroadcastReceiver() {

    private val myTag: String = GLOBAL_TAG + " " + BroadcastReceiver::class.java.simpleName

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val todoInteractors: TodoInteractors = TodoApp.instance.getTodoInteractions()

    override fun onReceive(context: Context, intent: Intent) {

        Timber.tag(myTag).d("onReceive")

        val id = intent.getIntExtra(NOTIFICATION_ID, 0)

        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("NotificationID: ${id}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Timber.tag(myTag).d("log $log context $context")
            }
        }

        if (id != 0) {
            CoroutineScope(ioDispatcher).launch {
                setTodoDone(id)
                context.apply {
                    // Remove the notification programmatically on button click
                    NotificationManagerCompat.from(this).cancel(id)
                }
            }
        }
    }

    private suspend fun setTodoDone(id: Int) = withContext(ioDispatcher){
        todoInteractors.setTodoDone(id)
    }


}