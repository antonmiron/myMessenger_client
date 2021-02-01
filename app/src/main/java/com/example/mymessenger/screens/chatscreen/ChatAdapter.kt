package com.example.mymessenger.screens.chatscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymessenger.R
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.network.models.MessageType
import kotlinx.android.synthetic.main.item_incoming_chat_message.view.*
import kotlinx.android.synthetic.main.item_information_chat_message.view.*
import kotlinx.android.synthetic.main.item_outgoing_chat_message.view.*
import java.text.SimpleDateFormat

class ChatAdapter(private val currentUserName: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageList =  mutableListOf<Message>()
    private val simpleDateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return when(message.type){
            MessageType.TEXT ->{
                if(message.userName == currentUserName) ChatMessageType.OUTGOING.value
                else ChatMessageType.INCOMING.value
            }
            else -> ChatMessageType.INFORMATION.value
        }
    }

    fun addNewMessage(message: Message){
        messageList.add(message)
        notifyItemInserted(messageList.size-1)
    }

    fun addNewMessages(newMessageList: List<Message>){
        if(newMessageList.isNullOrEmpty()) return

        val newMessagesCount = newMessageList.size

        messageList.addAll(newMessageList)
        notifyItemRangeInserted(messageList.size-newMessagesCount, newMessagesCount)
    }

    override fun getItemCount() = messageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(ChatMessageType.parse(viewType)){
            ChatMessageType.INCOMING -> IncomingMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_incoming_chat_message, parent, false))
            ChatMessageType.OUTGOING -> OutgoingMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_outgoing_chat_message, parent, false))
            ChatMessageType.INFORMATION -> InformationMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_information_chat_message, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is IncomingMessageViewHolder -> holder.bind(messageList[position])
            is OutgoingMessageViewHolder -> holder.bind(messageList[position])
            is InformationMessageViewHolder -> holder.bind(messageList[position])
        }
    }

    inner class IncomingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(message: Message){
            with(itemView){
                llIncomingChatMessage.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_message_left, null)

                tvIncomingChatUserNameText.text = message.userName
                tvIncomingChatMessageText.text = message.text
                tvIncomingChatMessageTimeText.text = simpleDateFormat.format(message.date)
            }
        }
    }

    inner class OutgoingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(message: Message){
            with(itemView){
                llOutgoingChatMessage.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_message_right, null)

                tvOutgoingChatMessageText.text = message.text
                tvOutgoingChatMessageTimeText.text = simpleDateFormat.format(message.date)
            }
        }
    }
    inner class InformationMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(message: Message){
            with(itemView){
                when(message.type){
                    MessageType.USER_ADDED -> tvInformationMessage.text = resources.getString(R.string.chat_tv_infoMessageUserJoin, message.userName)
                    MessageType.USER_REMOVED -> tvInformationMessage.text = resources.getString(R.string.chat_tv_infoMessageUserLeft, message.userName)
                }

            }
        }
    }

    enum class ChatMessageType(val value: Int) {
        INCOMING(0),
        OUTGOING(1),
        INFORMATION(2);

        companion object{
            fun parse(value: Int): ChatMessageType {
                values().forEach {
                    if(it.value == value) return it
                }
                throw IllegalArgumentException("Can not parse value: $value as instance of " + ChatMessageType::class.java.name)
            }
        }
    }
}