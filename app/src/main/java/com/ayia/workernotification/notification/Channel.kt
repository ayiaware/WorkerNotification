package com.ayia.workernotification.notification

class Channel(
    val channelId: String,
    val channelName: CharSequence,
    val channelDescription: String,
    val channelImportance: Int,
    val isChannelEnableVibrate: Boolean,
    val channelLockscreenVisibility: Int
)
