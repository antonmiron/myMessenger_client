package com.example.mymessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import com.example.mymessenger.network.ConnectionManager
import com.example.mymessenger.network.MessageManager
import com.example.mymessenger.network.models.Message
import com.example.mymessenger.network.models.MessageType
import com.example.mymessenger.tools.NotificationHelper
import com.example.mymessenger.tools.customobserver.SimpleObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val isKeyboardOpenLiveData = MutableLiveData<Boolean>(false)
    private var notificationHelper: NotificationHelper? = null

    private val newMessageNotificationObserver = object:SimpleObserver<Message>(){
        override fun onUpdate(value: Message?) {
            // if message is null jst return
            value?:return

            //if it not text message - return
            if(value.type != MessageType.TEXT) return

            val isAppResumed = this@MainActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            //if app is resumed - return
            if(isAppResumed) return

            notificationHelper?.showNewMessageNotification(value)

        }
    }

    @Inject
    lateinit var connectionManager: ConnectionManager
    @Inject
    lateinit var messageManager: MessageManager

    init {
        App.dependencyInjectorComponent.inject(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = NotificationHelper(this)
        messageManager.newMessageObservable.addObserver(newMessageNotificationObserver)

        connectionManager.registerNetworkStateCallback()

        /*Keyboard state listener*/
        var heightDiff = 0
        activityLayoutRoot.viewTreeObserver.addOnGlobalLayoutListener {
            if (heightDiff == activityLayoutRoot.rootView.height - activityLayoutRoot.height)
                return@addOnGlobalLayoutListener

            heightDiff = activityLayoutRoot.rootView.height - activityLayoutRoot.height

            //I assume that at least 10% of the screen will be filled with the keyboard
            val keyboardSize = activityLayoutRoot.height * 0.1F
            val keyboardSizeDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyboardSize, resources.displayMetrics)

            isKeyboardOpenLiveData.value = heightDiff > keyboardSizeDP
        }
    }

    override fun onStart() {
        super.onStart()
        notificationHelper?.clearAllNotifications()
    }

    /**
     * Hide keyboard
     * */
    fun hideKeyboardIfOpen() {
        currentFocus?.let {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    /**
     * Function create [Snackbar]
     *
     * @param view The view to find a parent from.
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Can be {@link #LENGTH_SHORT}, {@link
     * #LENGTH_LONG}, {@link #LENGTH_INDEFINITE}, or a custom duration in milliseconds.
     * @param topMargin margin top
     * */
    private fun createSnackbar(view: View, @StringRes resId: Int, duration: Int, topMargin: Int): Snackbar {
        view.bringToFront()
        val snackbar = Snackbar.make(view, resId, duration)

        (snackbar.view as Snackbar.SnackbarLayout).apply {
            layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                setMargins(0, topMargin, 0, 0)
            }
            setPadding(0, 0, 0, 0)
        }

        return snackbar
    }

    /**
     * Function show snackbar with message
     *
     * @param messageResId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Can be {@link #LENGTH_SHORT}, {@link
     * #LENGTH_LONG}, {@link #LENGTH_INDEFINITE}, or a custom duration in milliseconds.
     * @param topMargin margin top
     * */
    fun showSnackbarMessage(@StringRes messageResId: Int, duration: Int, topMargin: Int){
        createSnackbar(layoutCoordinator, messageResId, duration, topMargin).show()
    }

    override fun onDestroy() {
        notificationHelper = null
        messageManager.newMessageObservable.deleteObserver(newMessageNotificationObserver)
        connectionManager.unregisterNetworkStateCallback()
        super.onDestroy()
    }
}
