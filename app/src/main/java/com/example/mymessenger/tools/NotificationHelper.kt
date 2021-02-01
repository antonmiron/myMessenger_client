package com.example.mymessenger.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.example.mymessenger.MainActivity
import com.example.mymessenger.R
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.services.NotificationReplyService

class NotificationHelper(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var notificationMessageList = mutableListOf<Message>()

    init {
        createMessageChannel()
    }

    private fun createMessageChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val channel = NotificationChannel(
            CHANNEL_MESSAGE_ID,
            context.getString(R.string.notification_channel_description),
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        notificationManager.createNotificationChannel(channel)
    }

    fun showNewMessageNotification(message: Message){
        notificationMessageList.add(message)

        val notification = buildMessageNotification()
        showNotification(0, notification)
    }

    private fun buildMessageNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            CHANNEL_MESSAGE_ID
        )

        //Intent
        val contentIntent = Intent(context, NotificationReplyService::class.java).apply {
            action = ACTION_REPLY
            //put hash of the first message which we receive in background
            putExtra(EXTRA_ID, notificationMessageList.hashCode())
        }

        //reply pending intent
        val replyPendingIntent = PendingIntent.getService(context, 0, contentIntent, 0)

        //activity pending intent
        val activityPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)

        // RemoteInput
        val remoteInput: RemoteInput = RemoteInput.Builder(EXTRA_TEXT_REPLY)
            .setLabel(context.getString(R.string.notification_reply_message_label))
            .build()

        // Action
        val action = NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send, context.getString(R.string.notification_reply_message_button), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        //Style
        val messageStyle = NotificationCompat.MessagingStyle(Person.Builder().setName(context.getString(R.string.you)).build()).also {
            it.isGroupConversation = true
            for(message in notificationMessageList){
                it.addMessage(message.text, message.date.time, Person.Builder().setName(message.userName).build())
            }
        }

        notificationBuilder
            .setStyle(messageStyle)
            .setSmallIcon(R.drawable.ic_baseline_chat_24)
            .setContentIntent(activityPendingIntent)
            .addAction(action)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .priority = NotificationCompat.PRIORITY_MAX


        return notificationBuilder.build()
    }

    private fun showNotification(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    fun clearAllNotifications(){
        notificationMessageList.clear()
        notificationManager.cancelAll()
    }

    companion object{
        private const val CHANNEL_MESSAGE_ID = "CHANNEL_MESSAGE_ID"
        const val ACTION_REPLY = "message_action_reply"
        const val EXTRA_ID = "message_extra_id"
        const val EXTRA_TEXT_REPLY = "message_extra_text_reply"
    }

}