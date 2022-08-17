package com.ayia.workernotification.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.ayia.workernotification.*
import com.ayia.workernotification.presentation.MainActivity
import com.ayia.workernotification.util.CHANNEL_ID
import com.ayia.workernotification.util.GLOBAL_TAG
import com.ayia.workernotification.util.NOTIFICATION_ACTION_TODO_DONE
import com.ayia.workernotification.util.NOTIFICATION_ID
import timber.log.Timber

internal object NotifyUtils {

    private val myTag = GLOBAL_TAG + " " +  NotifyUtils::class.java.simpleName

    fun createReminderNotification(content: Array<String?>, id: Int, context: Context) {

        val name: CharSequence = context.getString(R.string.channel_reminder_name)

        val description = context.getString(R.string.channel_reminder_description)

        var channel = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val newChannel = Channel(CHANNEL_ID, name, description, importance,
                true, NotificationCompat.VISIBILITY_PRIVATE)

           channel = createNotificationChannel(context, newChannel)

        }

        val doneIntent = Intent(context, BroadcastReceiver::class.java).apply {
            action = NOTIFICATION_ACTION_TODO_DONE
            putExtra(NOTIFICATION_ID, id)
        }

        val donePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context, id, doneIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else  PendingIntent.FLAG_UPDATE_CURRENT
            )


        Timber.tag(myTag).d("createReminderNotification Title ${content[0]} Text ${content[1]}")


        val args = Bundle()
        args.putInt("todoId", id)

        //Create pending intent
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.TodoFormFragment)
            .setArguments(args)
            //Add this line if navigation graph is not contained in launch activity
            //.setComponentName(MainActivity::class.java)
            .createPendingIntent()

        //Create notification
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_reminder_active_24)
            .setContentTitle(content[0])
            .setContentText(content[1])
            .setContentIntent(pendingIntent) //attach pending intent to notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check_circle_filled, context.getString(R.string.label_done), donePendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(context).notify(id, builder.build())




    }

    private fun createNotificationChannel(context: Context, notificationData: Channel): String {

        val channelId: String = notificationData.channelId

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.

            // The user-visible name of the channel.
            val channelName: CharSequence = notificationData.channelName
            // The user-visible description of the channel.
            val channelDescription: String = notificationData.channelDescription
            val channelImportance: Int = notificationData.channelImportance
            val channelEnableVibrate: Boolean = notificationData.isChannelEnableVibrate
            val channelLockscreenVisibility: Int = notificationData.channelLockscreenVisibility

            // Initializes NotificationChannel.
            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)

            notificationChannel.description = channelDescription
            notificationChannel.enableVibration(channelEnableVibrate)
            notificationChannel.lockscreenVisibility = channelLockscreenVisibility

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
        return channelId
    }


}