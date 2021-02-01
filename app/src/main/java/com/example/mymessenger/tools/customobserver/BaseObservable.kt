package com.example.mymessenger.tools.customobserver

import java.util.*


open class BaseObservable<DATA_TYPE>(private var value: DATA_TYPE? = null) : Observable() {

    override fun notifyObservers() {
        synchronized(this) {
            setChanged()
            if (countObservers() != 0) {
                super.notifyObservers()
            }
        }
    }

    fun getValue(): DATA_TYPE? {
        synchronized(this) {
            return value
        }
    }

    fun getValue(defaultValue: DATA_TYPE): DATA_TYPE {
        synchronized(this) {
            if (value == null) {
                value = defaultValue
            }
            return value!!
        }
    }

    fun setValue(value: DATA_TYPE?) {
        synchronized(this) {
            if (value != this.value) {
                this.value = value
                notifyObservers()
            }
        }
    }

    fun clear(): BaseObservable<DATA_TYPE> {
        synchronized(this) {
            value = null
        }
        return this
    }


  fun addObserver(o: Observer?, refresh: Boolean) {
      super.addObserver(o)
      synchronized(this) {
          if (refresh) o?.update(this, null)
      }
  }
}