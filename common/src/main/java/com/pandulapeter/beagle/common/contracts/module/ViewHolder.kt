package com.pandulapeter.beagle.common.contracts.module

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Enforces a bind method for the standard [RecyclerView.ViewHolder].
 */
abstract class ViewHolder<T : Cell<T>>(view: View) : RecyclerView.ViewHolder(view) {

    /**
     * Called when the UI needs to be updated with new data represented by a [Cell] instance.
     * Make sure to re-bind everything that is contained in the model.
     *
     * @param model - The [Cell] implementation to bind.
     */
    abstract fun bind(model: T)

    /**
     * For internal use only.
     */
    @Suppress("UNCHECKED_CAST")
    fun forceBind(model: Cell<*>) = bind(model as T)
}