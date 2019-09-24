package com.pandulapeter.debugMenu.views

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pandulapeter.debugMenu.R
import com.pandulapeter.debugMenu.views.items.DrawerItemViewModel
import com.pandulapeter.debugMenu.views.items.button.ButtonViewHolder
import com.pandulapeter.debugMenu.views.items.button.ButtonViewModel
import com.pandulapeter.debugMenu.views.items.header.HeaderViewHolder
import com.pandulapeter.debugMenu.views.items.header.HeaderViewModel
import com.pandulapeter.debugMenu.views.items.listHeader.ListHeaderViewHolder
import com.pandulapeter.debugMenu.views.items.listHeader.ListHeaderViewModel
import com.pandulapeter.debugMenu.views.items.listItem.ListItemViewHolder
import com.pandulapeter.debugMenu.views.items.listItem.ListItemViewModel
import com.pandulapeter.debugMenu.views.items.logItem.LogItemViewHolder
import com.pandulapeter.debugMenu.views.items.logItem.LogItemViewModel
import com.pandulapeter.debugMenu.views.items.longText.LongTextViewHolder
import com.pandulapeter.debugMenu.views.items.longText.LongTextViewModel
import com.pandulapeter.debugMenu.views.items.networkLogItem.NetworkLogItemViewHolder
import com.pandulapeter.debugMenu.views.items.networkLogItem.NetworkLogItemViewModel
import com.pandulapeter.debugMenu.views.items.singleSelectionListItem.SingleSelectionListItemViewHolder
import com.pandulapeter.debugMenu.views.items.singleSelectionListItem.SingleSelectionListItemViewModel
import com.pandulapeter.debugMenu.views.items.text.TextViewHolder
import com.pandulapeter.debugMenu.views.items.text.TextViewModel
import com.pandulapeter.debugMenu.views.items.toggle.ToggleViewHolder
import com.pandulapeter.debugMenu.views.items.toggle.ToggleViewModel

internal class DebugMenuAdapter : ListAdapter<DrawerItemViewModel, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<DrawerItemViewModel>() {

    override fun areItemsTheSame(oldItem: DrawerItemViewModel, newItem: DrawerItemViewModel) = oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DrawerItemViewModel, newItem: DrawerItemViewModel) = oldItem == newItem

    override fun getChangePayload(oldItem: DrawerItemViewModel, newItem: DrawerItemViewModel): Any? = if (oldItem.shouldUsePayloads && newItem.shouldUsePayloads) "" else null
}) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is TextViewModel -> R.layout.item_text
        is LongTextViewModel -> R.layout.item_long_text
        is ToggleViewModel -> R.layout.item_toggle
        is ButtonViewModel -> R.layout.item_button
        is ListHeaderViewModel -> R.layout.item_list_header
        is ListItemViewModel<*> -> R.layout.item_list_item
        is SingleSelectionListItemViewModel<*> -> R.layout.item_single_selection_list_item
        is HeaderViewModel -> R.layout.item_header
        is NetworkLogItemViewModel -> R.layout.item_network_log
        is LogItemViewModel -> R.layout.item_log
        else -> throw IllegalArgumentException("Unsupported item type at position $position.")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_text -> TextViewHolder.create(parent)
        R.layout.item_long_text -> LongTextViewHolder.create(parent)
        R.layout.item_toggle -> ToggleViewHolder.create(parent)
        R.layout.item_button -> ButtonViewHolder.create(parent)
        R.layout.item_list_header -> ListHeaderViewHolder.create(parent)
        R.layout.item_list_item -> ListItemViewHolder.create(parent)
        R.layout.item_single_selection_list_item -> SingleSelectionListItemViewHolder.create(parent)
        R.layout.item_header -> HeaderViewHolder.create(parent)
        R.layout.item_network_log -> NetworkLogItemViewHolder.create(parent)
        R.layout.item_log -> LogItemViewHolder.create(parent)
        else -> throw IllegalArgumentException("Unsupported view type: $viewType.")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is TextViewHolder -> holder.bind(getItem(position) as TextViewModel)
        is LongTextViewHolder -> holder.bind(getItem(position) as LongTextViewModel)
        is ToggleViewHolder -> holder.bind(getItem(position) as ToggleViewModel)
        is ButtonViewHolder -> holder.bind(getItem(position) as ButtonViewModel)
        is ListHeaderViewHolder -> holder.bind(getItem(position) as ListHeaderViewModel)
        is ListItemViewHolder -> holder.bind(getItem(position) as ListItemViewModel<*>)
        is SingleSelectionListItemViewHolder -> holder.bind(getItem(position) as SingleSelectionListItemViewModel<*>)
        is HeaderViewHolder -> holder.bind(getItem(position) as HeaderViewModel)
        is NetworkLogItemViewHolder -> holder.bind(getItem(position) as NetworkLogItemViewModel)
        is LogItemViewHolder -> holder.bind(getItem(position) as LogItemViewModel)
        else -> throw IllegalArgumentException("Unsupported item type at position $position.")
    }
}