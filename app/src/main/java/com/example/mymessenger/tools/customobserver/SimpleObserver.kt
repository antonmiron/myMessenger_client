package com.example.mymessenger.tools.customobserver

import java.util.*


abstract class SimpleObserver<DATA_TYPE> : Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (o is BaseObservable<*>) {
            @Suppress("UNCHECKED_CAST")
            onUpdate(o.getValue() as DATA_TYPE)
        }
    }

    abstract fun onUpdate(value: DATA_TYPE?)
}