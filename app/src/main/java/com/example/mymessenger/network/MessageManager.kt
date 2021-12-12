package com.example.mymessenger.network


import com.example.mymessenger.di.app.AppScope
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.network.models.MessageType.*
import com.example.mymessenger.tools.customobserver.BaseObservable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

@AppScope
class MessageManager @Inject constructor() {
    private var inputDisposable: Disposable? = null
    private var outputDisposable: Disposable? = null

    //Queue for output messages
    private val outputMessageQueue = LinkedBlockingQueue<Message>()

    private var rootJob = SupervisorJob()
    private var messageManagerScope = createScope()

    //Cache of all messages
    val messageCacheList = mutableListOf<Message>()
    //Observer to new message
    val newMessageObservable = BaseObservable<Message>()
    //Observer to IO exception
    val messageIOErrors = BaseObservable<Unit>()

    fun setInputStream(objectInputStream: ObjectInputStream) {
        inputDisposable?.dispose()
        inputDisposable = catchInputMessages(objectInputStream)
    }

    fun setOutputStream(objectOutputStream: ObjectOutputStream) {
        outputDisposable?.dispose()
        outputDisposable = sendOutputMessages(objectOutputStream)
    }

    private fun catchInputMessages(input: ObjectInputStream): Disposable {
        return Flowable.create<Message>({ emitter ->
            try {
                while (true) {
                    val message = input.readObject() as Message
                    emitter.onNext(message)
                }
            } catch (ex: Exception) {
                if (!emitter.isCancelled) emitter.onError(ex)
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::parseIncomingServerMessage, this::catchErrors)

    }

    private fun sendOutputMessages(output: ObjectOutputStream): Disposable {

        return Flowable.create<Message>({ emitter ->
            try {
                while (true) {
                    val message = outputMessageQueue.take()

                    output.writeObject(message)
                    output.flush()

                    emitter.onNext(message)
                }
            } catch (ex: Exception) {
                if (!emitter.isCancelled)
                    emitter.onError(ex)
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, this::catchErrors)
    }

    /**
     * Send message to the server
     *
     * p.s. the "put" method is blocking,
     * but we use it within the [messageManagerScope] and everything is ok
     * */
    @Suppress("BlockingMethodInNonBlockingContext")
    private fun sendMessage(message: Message) {
        messageManagerScope.launch {
            outputMessageQueue.put(message)
        }
    }

    fun sendPasswordMessage(password: String) {
        sendMessage(
            Message(type = PASSWORD, date = Date(), userName = null, text = password)
        )
    }

    fun sendUsernameMessage(userName: String) {
        sendMessage(
            Message(type = USER_NAME, date = Date(), userName = null, text = userName)
        )
    }

    fun sendTextMessage(text: String) {
        sendMessage(
            Message(type = TEXT, date = Date(), userName = null, text = text)
        )
    }

    fun release() {
        rootJob.cancel()

        inputDisposable?.dispose()
        outputDisposable?.dispose()

        outputMessageQueue.clear()
        messageCacheList.clear()
        newMessageObservable.clear()

        //recreate scope for reuse
        messageManagerScope = createScope()
    }


    private fun parseIncomingServerMessage(message: Message) {
        when (message.type) {
            TEXT, USER_ADDED, USER_REMOVED -> messageCacheList.add(message)
            else -> {
                /**
                 * dont save [PASSWORD], [PASSWORD_ACCEPTED] and etc. to the cache!
                 * */
            }
        }

        newMessageObservable.setValue(message)
    }

    private fun catchErrors(throwable: Throwable) {
        messageIOErrors.notifyObservers()
    }

    private fun createScope(): CoroutineScope {
        if (rootJob.isCancelled) {
            rootJob = SupervisorJob()
        }
        return CoroutineScope(Dispatchers.IO + rootJob)
    }
}