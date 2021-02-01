package com.example.mymessenger.tools.validators

import org.junit.Test
import org.junit.Assert.*

class ConnectionCredentialValidatorTest {

    /**test server address validator**/

    @Test
    fun validateServerAddress_isNotNull(){
        val isNull = ConnectionCredentialValidator.validateServerAddress(null) == null
        assertTrue(isNull)
    }

    @Test
    fun validateServerAddress_isNotDomain(){
        val isNull = ConnectionCredentialValidator.validateServerAddress("qwe.com") == null
        assertTrue(isNull)
    }

    @Test
    fun validateServerAddress_isValidIp(){
        var isNull = ConnectionCredentialValidator.validateServerAddress("123") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerAddress("123.123") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerAddress("123.123.123") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerAddress("1273.123.123.123") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerAddress("256.123.123.123") == null
        assertTrue(isNull)

        val value = ConnectionCredentialValidator.validateServerAddress("192.168.0.1")
        assertEquals("192.168.0.1", value)
    }

   /**test server port validator**/

    @Test
    fun validateServerPort_isNotNull(){
        val isNull = ConnectionCredentialValidator.validateServerPort(null) == null
        assertTrue(isNull)
    }

    @Test
    fun validateServerPort_isNumber(){
        val isNull = ConnectionCredentialValidator.validateServerPort("123qwerty") == null
        assertTrue(isNull)

        val isNotNull = ConnectionCredentialValidator.validateServerPort("29") != null
        assertTrue(isNotNull)
    }

    @Test
    fun validateServerPort_isValid(){
        var isNull = ConnectionCredentialValidator.validateServerPort("-1") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerPort("0") == null
        assertTrue(isNull)

        isNull = ConnectionCredentialValidator.validateServerPort("65536") == null
        assertTrue(isNull)

        val value = ConnectionCredentialValidator.validateServerPort("8080")
        assertEquals(8080, value)
    }

    /**test user name validator**/

    @Test
    fun validateUserName_isNotNull(){
        val isNull = ConnectionCredentialValidator.validateUserName(null) == null
        assertTrue(isNull)
    }

    @Test
    fun validateUserName_isValid(){
        //whitespaces
        var isNull = ConnectionCredentialValidator.validateUserName("      ") == null
        assertTrue(isNull)

        //trim, less 3 symbols
        isNull = ConnectionCredentialValidator.validateUserName("      I  ") == null
        assertTrue(isNull)

        //less 3 symbols
        isNull = ConnectionCredentialValidator.validateUserName("Iv") == null
        assertTrue(isNull)

        //more 16 symbols
        isNull = ConnectionCredentialValidator.validateUserName("IvanIvanIvanIvanIvanIvan") == null
        assertTrue(isNull)

        val value = ConnectionCredentialValidator.validateUserName("Ivan")
        assertEquals("Ivan", value)
    }

    /**test password validator**/
    @Test
    fun validatePassword_isNotNull(){
        val isNull = ConnectionCredentialValidator.validatePassword(null) == null
        assertTrue(isNull)
    }
}