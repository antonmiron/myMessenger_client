package com.example.mymessenger.view.fragments.chatfragment

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatLinearLayoutManager: LinearLayoutManager {
    constructor(context: Context): super(context)
    constructor(context: Context, @RecyclerView.Orientation orientation: Int,
                reverseLayout: Boolean): super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
                defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * strange behavior recycler view inside constraint layout
     *
     * https://stackoverflow.com/questions/52487038/recyclerview-changes-its-scroll-position-when-inside-a-constraintlayout
     * */
    override fun isAutoMeasureEnabled() = false
}