package com.example.mymessenger.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.example.mymessenger.view.app.App
import com.example.mymessenger.R
import com.example.mymessenger.di.app.AppScope
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.network.models.MessageType.*
import com.example.mymessenger.view.fragments.startfragment.models.ConnectionCredentialData
import com.example.mymessenger.view.fragments.startfragment.models.ConnectionStatus
import com.example.mymessenger.tools.customobserver.SimpleObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

@AppScope
class ConnectionManager @Inject constructor(private val messageManager: MessageManager) {
    private var socket: Socket? = null
    private var connectDisposable: Disposable? = null
    private val handler = Handler(Looper.getMainLooper())

    val connectionState = MutableLiveData<ConnectionStatus>()
    val connectionStateError = MutableLiveData<Int>()

    //Connection observer for sending credential
    private val connectionMessageObserver = object: SimpleObserver<Message>(){
        override fun onUpdate(value: Message?) {
            value?:return

            when(value.type){
                PASSWORD_REQUEST, NAME_REQUEST -> connectionState.value = ConnectionStatus.CONNECTING

                //password not accepted, close socket for change the credentials and connect again
                PASSWORD_NOT_ACCEPTED -> {
                    showConnectError(R.string.error_password_not_accepted)
                    disconnect()
                }

                //name not accepted, close socket for change the credentials and connect again
                NAME_NOT_ACCEPTED -> {
                    showConnectError(R.string.error_name_not_accepted)
                    disconnect()
                }


                //password - accepted, name - accepted -> successful connection
                NAME_ACCEPTED -> {
                    connectionState.value = ConnectionStatus.CONNECTED
                    messageManager.newMessageObservable.deleteObserver(this)
                }
                else ->{}
            }
        }
    }

    //Connection error observer
    private val connectionMessageIOErrorObserver = object: SimpleObserver<Unit>(){
        override fun onUpdate(value: Unit?) {
            showConnectError(R.string.error_something_went_wrong)
            disconnect()
        }
    }

    //Network state callback (EXIST, NOT EXIST)
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            //run in main thread
            handler.post { disconnect() }
        }
    }

    var connectionCredentialData: ConnectionCredentialData? = null

    /**
     * Connect to the server
     *
     * @param connectionCredentialData - connection credential
     * */
    fun connect(connectionCredentialData: ConnectionCredentialData) {
        this.connectionCredentialData = connectionCredentialData
        messageManager.newMessageObservable.addObserver(connectionMessageObserver)
        messageManager.messageIOErrors.addObserver(connectionMessageIOErrorObserver)

        connectionState.value = ConnectionStatus.CONNECTING

        //dispose previous connecting (if it exist)
        connectDisposable?.dispose()
        connectDisposable = Completable.create {
            socket = Socket().apply {
                connect(InetSocketAddress(connectionCredentialData.serverAddress, connectionCredentialData.serverPort), 5000)

                messageManager.setInputStream(ObjectInputStream(getInputStream()))
                messageManager.setOutputStream(ObjectOutputStream(getOutputStream()))
            }

            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},{
                showConnectError(R.string.error_connection_timeout)
                disconnect()
            })


    }

    /**
     * Disconnecting from the server
     * */
    fun disconnect() {
        connectionCredentialData = null
        //remove observer
        messageManager.newMessageObservable.deleteObserver(connectionMessageObserver)
        //remove observer
        messageManager.messageIOErrors.deleteObserver(connectionMessageIOErrorObserver)
        //clear value
        messageManager.newMessageObservable.clear()
        connectionState.value = ConnectionStatus.DISCONNECTED
        try {
            socket?.close() //close socket
        }catch (ex: Exception){} //ok ignore it
    }

    /**
     * Register network callback
     * @see networkCallback
     * */
    fun registerNetworkStateCallback() {
        val connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val builder = NetworkRequest.Builder()

        connectivityManager?.registerNetworkCallback(builder.build(), networkCallback)
    }

    /**
     * Unregister network callback
     * @see networkCallback
     * */
    fun unregisterNetworkStateCallback(){
        val connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private fun showConnectError(@StringRes errorStringRes: Int){
        connectionStateError.value = errorStringRes
    }

}