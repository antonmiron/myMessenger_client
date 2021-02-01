package com.example.mymessenger.tools.validators

import java.util.regex.Pattern

object ConnectionCredentialValidator {
    /**
     * Function validate address by ip pattern (example: 192.168.0.1)
     *
     * @param serverAddress - server address for validate
     * @return serverAddress - if ip address correct, null - in another case
     * */
    fun validateServerAddress(serverAddress: String?): String? {
        if (serverAddress.isNullOrBlank()) return null

        val ipAddressPattern =
            ("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))")

        val isServerAddressValid = Pattern.compile(ipAddressPattern).matcher(serverAddress).matches()
        return if (isServerAddressValid) serverAddress else null
    }

    /**
     * Function validate port by pattern (more 0, less 65535)
     *
     * @param serverPort - server port for validate
     * @return serverPort - if port correct, null - in another case
     * */
    fun validateServerPort(serverPort: String?): Int? {
        if(serverPort.isNullOrBlank()) return null

        //check: is it number?
        val isServerPortNumber = Pattern.compile("\\d+").matcher(serverPort).matches()

        return if(isServerPortNumber){
            val serverPortInt = serverPort.toInt()

            //check: is range valid?
            if(serverPortInt in 1..65535)
                serverPortInt
            else null
        } else null
    }

    /**
     * Function validate userName by pattern (more than 2, less than 17 excluding spaces at the edges)
     *
     * @param userName - user name for validate
     * @return userName - if user name correct, null - in another case
     * */
    fun validateUserName(userName: String?): String? {
        if(userName.isNullOrBlank()) return null

        val trimmedUserName = userName.trim()
        if(trimmedUserName.length < 3 || trimmedUserName.length > 16) return null

        return trimmedUserName
    }

    /**
     * Function validate password
     *
     * @param password - password for validate
     * @return password - if password correct, null - in another case
     * */
    fun validatePassword(password: String?): String? {
        return password
    }
}