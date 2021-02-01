package com.example.mymessenger.screens.startscreen


import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.mymessenger.R
import com.example.mymessenger.screens.BaseFragment
import com.example.mymessenger.screens.startscreen.models.ConnectionCredentialData
import com.example.mymessenger.screens.startscreen.models.ConnectionStatus
import com.example.mymessenger.tools.validators.ConnectionCredentialValidator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_start.*


class StartFragment : BaseFragment<StartViewModel>() {
    override var vm: StartViewModel? = null
    private var isConnectButtonClicked: Boolean? = null

    private val serverAddressTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (il_serverAddress.error != null)
                il_serverAddress.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val serverPortTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (il_serverPort.error != null)
                il_serverPort.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val serverUserNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (il_userName.error != null)
                il_userName.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val serverPasswordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (il_password.error != null)
                il_password.error = null
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Observer for connection status
     * show/hide progress overlay,
     * if connect is success -> go to chat fragment
     * **/
    private val connectionStatusObserver = Observer<ConnectionStatus> { status: ConnectionStatus? ->
        when (status) {
            ConnectionStatus.CONNECTING -> {
            }
            ConnectionStatus.CONNECTED -> findNavController().navigate(R.id.action_startFragment_to_chatFragment)
            ConnectionStatus.DISCONNECTED -> stopConnectWithAnimation()
        }
    }

    /**
     * Observer for connection errors
     * */
    private val connectionErrorObserver = Observer<Int> { messageRes: Int? ->
        messageRes?.let { showSnackbarMessage(it, Snackbar.LENGTH_LONG, 0) }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProviders.of(this).get(StartViewModel::class.java)

        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm?.connectionManager?.connectionState?.observe(
            viewLifecycleOwner,
            connectionStatusObserver
        )
        vm?.connectionManager?.connectionStateError?.observe(
            viewLifecycleOwner,
            connectionErrorObserver
        )

        et_serverAddress.addTextChangedListener(serverAddressTextWatcher)
        et_serverPort.addTextChangedListener(serverPortTextWatcher)
        et_userName.addTextChangedListener(serverUserNameTextWatcher)
        et_password.addTextChangedListener(serverPasswordTextWatcher)


        btnConnect.setOnClickListener {
            btnConnectOnClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        vm?.connectionManager?.connectionState?.removeObserver(connectionStatusObserver)
        vm?.connectionManager?.connectionStateError?.removeObserver(connectionErrorObserver)
    }

    /**
     * Function for get the valid connection inputs data
     *
     * @return [ConnectionCredentialData] or null if data not valid
     * */
    private fun getConnectionData(): ConnectionCredentialData? {
        val serverAddress =
            ConnectionCredentialValidator.validateServerAddress(et_serverAddress.text?.toString())
        val serverPort =
            ConnectionCredentialValidator.validateServerPort(et_serverPort.text?.toString())
        val userName =
            ConnectionCredentialValidator.validateUserName(et_userName.text?.toString())
        val password =
            ConnectionCredentialValidator.validatePassword(et_password.text?.toString())

        if (serverAddress == null)
            il_serverAddress.error = getString(R.string.start_et_error_serverAddress)

        if (serverPort == null)
            il_serverPort.error = getString(R.string.start_et_error_serverPort)

        if (userName == null)
            il_userName.error = getString(R.string.start_et_error_userName)

        if (password == null)
            il_password.error = getString(R.string.start_et_error_password)

        return if (serverAddress != null && serverPort != null && userName != null && password != null)
            ConnectionCredentialData(serverAddress, serverPort, userName, password)
        else null
    }

    private fun getHeaderLoadingAnimator(duration: Long, reverse: Boolean = false): ValueAnimator {
        val defaultHeaderHeight = resources.getDimension(R.dimen.size176).toInt()
        val newHeaderHeight = layoutRoot.height
        val from = if (reverse) newHeaderHeight else defaultHeaderHeight
        val to = if (reverse) defaultHeaderHeight else newHeaderHeight
        val valueAnimator = ValueAnimator
            .ofInt(from, to)
            .setDuration(duration)

        /**
         * code for ignoring the duplicated animation of the folding/unfolding
         * example: header is unfolded - not unfold it again (until it is folded)
         *          header is folded - not fold it again (until it is unfolded)
         * */
        val ignoreUnfolding = reverse && layoutHeader.layoutParams.height == defaultHeaderHeight
        val ignoreFolding = !reverse && layoutHeader.layoutParams.height == newHeaderHeight

        if (!ignoreFolding && !ignoreUnfolding) {
            valueAnimator.addUpdateListener { anim ->
                val value = anim.animatedValue as Int
                layoutHeader.layoutParams.height = value
                layoutHeader.requestLayout()
            }
        }

        return valueAnimator
    }

    private fun getHeaderKeyboardAnimator(duration: Long, reverse: Boolean = false): ValueAnimator {
        val layoutBodyMarginParams = layoutBody.layoutParams as ViewGroup.MarginLayoutParams
        val defaultHeaderHeight = resources.getDimension(R.dimen.size176).toInt()
        val from = if (reverse) 0 else defaultHeaderHeight
        val to = if (reverse) defaultHeaderHeight else 0
        val valueAnimator = ValueAnimator
            .ofInt(from, to)
            .setDuration(duration)

        /**
         * code for ignoring the duplicated animation of the folding/unfolding
         * example: header is unfolded - not unfold it again (until it is folded)
         *          header is folded - not fold it again (until it is unfolded)
         * */
        val ignoreUnfolding = reverse && layoutHeader.layoutParams.height == defaultHeaderHeight
        val ignoreFolding = !reverse && layoutHeader.layoutParams.height == 0

        if (!ignoreFolding && !ignoreUnfolding) {
            valueAnimator.addUpdateListener { anim ->
                val value = anim.animatedValue as Int

                layoutHeader.layoutParams.height = value
                layoutBodyMarginParams.topMargin = value

                layoutRoot.requestLayout()
            }
        }

        return valueAnimator
    }

    private fun btnConnectOnClick() {
        if (isKeyboardOpen()) {
            hideKeyboardIfOpen()
            isConnectButtonClicked = true
        } else {
            startConnectWithAnimation()
        }
    }

    private fun startConnectWithAnimation() {
        getConnectionData()?.let { connectionData ->
            getHeaderLoadingAnimator(1000).apply {
                doOnEnd {
                    progressBarConnection.visibility = View.VISIBLE
                    vm?.onClickConnect(connectionData)
                }
                start()
            }
        }
    }

    private fun stopConnectWithAnimation() {
        getHeaderLoadingAnimator(1000, true).apply {
            progressBarConnection.visibility = View.GONE
            start()
        }
    }

    override fun keyboardOpened() {
        getHeaderKeyboardAnimator(500).start()
    }

    override fun keyboardClosed() {
        getHeaderKeyboardAnimator(500, true).apply {
            doOnEnd {
                if (isConnectButtonClicked == true) {
                    isConnectButtonClicked = false
                    startConnectWithAnimation()
                }
            }

            start()
        }
    }

}
