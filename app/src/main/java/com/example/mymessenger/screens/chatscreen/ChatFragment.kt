package com.example.mymessenger.screens.chatscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymessenger.R
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.screens.BaseFragment
import com.example.mymessenger.screens.startscreen.models.ConnectionStatus
import com.example.mymessenger.services.NotificationReplyService
import com.example.mymessenger.tools.customobserver.SimpleObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : BaseFragment<ChatViewModel>() {
    override var vm: ChatViewModel? = null


    private val dialogLogout by lazy { createLogOutDialog() }
    private val connectionStatusObserver = Observer<ConnectionStatus> { status: ConnectionStatus? ->
        when(status){
            ConnectionStatus.CONNECTED ->
                activity?.startService(Intent(activity, NotificationReplyService::class.java))
            ConnectionStatus.CONNECTING -> {}
            ConnectionStatus.DISCONNECTED -> {
                activity?.stopService(Intent(activity, NotificationReplyService::class.java))
                vm?.messageManager?.release()
                findNavController().navigate(R.id.action_chatFragment_to_startFragment)
            }
        }
    }
    private val chatMessagesListObserver = object: SimpleObserver<Message>() {
        override fun onUpdate(value: Message?) {
            value?.let{
                chatAdapter.addNewMessage(it)
            }
        }
    }

    private val chatAdapter by lazy {
        ChatAdapter(vm?.getCurrentUserName())
    }
    private val chatLinearLayoutManager by lazy {
        ChatLinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        ).apply { stackFromEnd = true }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setSupportActionBar(toolbar)
        vm?.connectionManager?.connectionState?.observe(viewLifecycleOwner, connectionStatusObserver)

        btnExit.setOnClickListener { dialogLogout.show() }
        with(rvChatMessages) {
            adapter = chatAdapter.apply {
                registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        val lastVisibleItemPosition = chatLinearLayoutManager.findLastVisibleItemPosition()
                        val newItemPosition = getItemCount()-1

                        //Here we check current position with last visible and show new or stay on last
                        if(lastVisibleItemPosition >= newItemPosition -1 )
                            rvChatMessages.scrollToPosition(newItemPosition)
                    }
                })
            }

            layoutManager = chatLinearLayoutManager
        }

        vm?.messageManager?.let{
            chatAdapter.addNewMessages(it.messageCacheList)
            it.newMessageObservable.addObserver(chatMessagesListObserver)
        }

        ilTextMessage.setEndIconOnClickListener {
            val message = etTextMessage.text?.toString()
            if(message.isNullOrBlank()) return@setEndIconOnClickListener

            vm?.sendMessage(message)
            etTextMessage.text?.clear()
        }
    }

    private fun createLogOutDialog(): AlertDialog {
        hideKeyboardIfOpen()

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_chat_logout_title)
            .setMessage(R.string.dialog_chat_logout_message)
            .setPositiveButton(R.string.ok){ dialog, _ -> vm?.logout(); dialog.dismiss()}
            .setNegativeButton(R.string.cancel){ dialog, _ -> dialog.dismiss() }
            .create()
    }

    override fun onDestroyView() {
        rvChatMessages.adapter = null
        //prevent leaking
        mainActivity.setSupportActionBar(null)

        vm?.connectionManager?.connectionState?.removeObserver(connectionStatusObserver)
        vm?.messageManager?.newMessageObservable?.deleteObserver(chatMessagesListObserver)
        super.onDestroyView()
    }
}
