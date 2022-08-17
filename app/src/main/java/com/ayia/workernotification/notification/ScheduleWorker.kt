package com.ayia.workernotification.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoApp
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.util.NOTIFICATION_ID
import com.ayia.workernotification.util.NOTIFICATION_TITLE
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber


class ScheduleWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Timber.tag(myTag).d("doWork()")

        val applicationContext = applicationContext

        return try {

            val title = inputData.getString(NOTIFICATION_TITLE)
            val id = inputData.getInt(NOTIFICATION_ID, 0)

            withContext(IO) {

                val todo: Todo? = TodoApp.instance.getTodoInteractions().getTodo(id)

                Timber.tag(myTag).d("Notified Todo $todo ID $id")

                if (todo?.deadline != null && todo.isReminderOn) {

                    val text = todo.title

                    Timber.tag(myTag).d("Notified Todo %s", text)

                    NotifyUtils.createReminderNotification(
                        arrayOf(title, text), id,
                        applicationContext
                    )
                }



            }
            Result.success()

        } catch (throwable: Throwable) {
            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Timber.tag(myTag).e(throwable, "Error scheduling notification")
            Result.failure()
        }
    }

    companion object {
        private val myTag = GLOBAL_TAG + " " + ScheduleWorker::class.java.simpleName
    }
}
