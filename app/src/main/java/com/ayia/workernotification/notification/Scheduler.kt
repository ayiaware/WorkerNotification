package com.ayia.workernotification.notification

import android.content.Context
import androidx.work.*
import com.ayia.workernotification.*
import com.ayia.workernotification.R
import com.ayia.workernotification.domain.Todo
import com.ayia.workernotification.framework.TodoApp
import com.ayia.workernotification.util.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Scheduler(private val todo: Todo) {

    private val myTag: String = GLOBAL_TAG + "-" + Scheduler::class.java.simpleName

    private val mWorkManager: WorkManager

    var title : String

    init {

        val context: Context = TodoApp.instance

        mWorkManager = WorkManager.getInstance(context)

        title = context.getString(R.string.label_todo)


    }


    fun schedule(){

        if (todo.deadline != null){

            val initialDate = System.currentTimeMillis()
            Timber.tag(myTag).d("initialDate : %s", toDateTimeString(initialDate))

            val reminderDate: Long = todo.deadline!!
            Timber.tag(myTag).d("reminderDate : %s", toDateTimeString(reminderDate))

            val dateDifference = (reminderDate - initialDate)
            Timber.tag(myTag).d("dateDifference : %s", dateDifference)

            applyNotificationDelay(dateDifference)
        }
        else cancelWork()
    }


    fun removeReminder(){
        cancelWork()
    }




    /**
     * Create the WorkRequest to apply delay to notification creation till reminder date
     * @param delay The time till remainder date
     */
    private fun applyNotificationDelay(delay: Long) {

        Timber.tag(myTag).d("applyNotificationDelay")

        val workRequest = OneTimeWorkRequest.Builder(ScheduleWorker::class.java)
            .setInputData(createInputDataForNotification())
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(NOTIFICATION_SCHEDULE_WORK_NAME)
            .build()

        mWorkManager.enqueueUniqueWork(todo.id.toString(), ExistingWorkPolicy.REPLACE, workRequest)

    }

    private fun cancelWork() {

        Timber.tag(myTag).d("cancelWork")

        mWorkManager.cancelUniqueWork(todo.id.toString())
    }


    /**
     * Creates the input data bundle which includes the tile, message and  notification id
     * @return Data which contains the Notification details
     */
    private fun createInputDataForNotification(): Data {

        Timber.tag(myTag).d("createInputDataForNotification")

        val builder = Data.Builder()
        builder.putString(NOTIFICATION_TITLE, title)
        builder.putInt(NOTIFICATION_ID, todo.id)
        return builder.build()
    }


}