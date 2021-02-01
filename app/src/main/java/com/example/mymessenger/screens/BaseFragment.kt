package com.example.mymessenger.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.example.mymessenger.MainActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VM_TYPE : BaseViewModel> : Fragment() {
    protected abstract var vm: VM_TYPE?
    val mainActivity: MainActivity
        get() {
            val mActivity = activity as? MainActivity
            return mActivity ?: throw NullPointerException(
                "Activity is null. Maybe wrong place call."
            )
        }

    private val keyboardStateObserver = Observer<Boolean>{ isOpen:Boolean?->
        if(isOpen == true) keyboardOpened()
        else keyboardClosed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.isKeyboardOpenLiveData.observe(viewLifecycleOwner, keyboardStateObserver)
    }

    override fun onDestroyView() {
        mainActivity.isKeyboardOpenLiveData.removeObserver(keyboardStateObserver)
        super.onDestroyView()
    }

    open fun keyboardOpened(){}
    open fun keyboardClosed(){}

    fun hideKeyboardIfOpen(){
        mainActivity.hideKeyboardIfOpen()
    }
    fun isKeyboardOpen() = mainActivity.isKeyboardOpenLiveData.value == true

    /**
     * Function show snackbar with message
     *
     * @param messageResId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Can be {@link #LENGTH_SHORT}, {@link
     * #LENGTH_LONG}, {@link #LENGTH_INDEFINITE}, or a custom duration in milliseconds.
     * @param topMargin margin top (default 0)
     * */
    fun showSnackbarMessage(@StringRes messageResId: Int, duration: Int, topMargin: Int = 0){
        mainActivity.showSnackbarMessage(messageResId, duration, topMargin)
    }
}