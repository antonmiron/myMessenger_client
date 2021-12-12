package com.example.mymessenger.services


import android.app.Service
import android.content.Intent
import androidx.core.app.RemoteInput
import com.example.mymessenger.tools.NotificationHelper

class NotificationReplyService: Service() {
  /*  @Inject
    lateinit var messageManager: MessageManager
*/
    init {
    //    App.dependencyInjectorComponent.inject(this)
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == NotificationHelper.ACTION_REPLY){
            val replyText: String? = RemoteInput.getResultsFromIntent(intent)?.getString(NotificationHelper.EXTRA_TEXT_REPLY)
          //  replyText?.let{ messageManager.sendTextMessage(it) }
        }
        return START_STICKY
    }
}