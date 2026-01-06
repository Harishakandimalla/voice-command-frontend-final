package com.example.voicecommandaiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicecommandaiapp.R
import com.example.voicecommandaiapp.model.NotificationItem

/* ---------------- SEALED LIST ITEMS ---------------- */

sealed class NotificationListItem {
    data class Header(val title: String, val count: Int) : NotificationListItem()
    data class Notification(val item: NotificationItem) : NotificationListItem()
}

/* ---------------- ADAPTER ---------------- */

class NotificationsAdapter(
    private val onItemClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<NotificationListItem>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTIFICATION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationListItem.Header -> TYPE_HEADER
            is NotificationListItem.Notification -> TYPE_NOTIFICATION
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER ->
                HeaderViewHolder(
                    inflater.inflate(
                        R.layout.item_task_section_header,
                        parent,
                        false
                    )
                )

            TYPE_NOTIFICATION ->
                NotificationViewHolder(
                    inflater.inflate(
                        R.layout.item_notification,
                        parent,
                        false
                    )
                )

            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = items[position]) {

            is NotificationListItem.Header ->
                (holder as HeaderViewHolder).bind(item)

            is NotificationListItem.Notification -> {
                (holder as NotificationViewHolder).bind(item.item)
                holder.itemView.setOnClickListener {
                    onItemClick(item.item)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    /* ---------------- PUBLIC API ---------------- */

    fun submitList(listItems: List<NotificationListItem>) {
        items.clear()
        items.addAll(listItems)
        notifyDataSetChanged()
    }

    /* ---------------- VIEW HOLDERS ---------------- */

    class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val title: TextView =
            itemView.findViewById(R.id.tv_section_title)

        private val subtitle: TextView =
            itemView.findViewById(R.id.tv_section_subtitle)

        fun bind(header: NotificationListItem.Header) {
            title.text = header.title
            subtitle.text = "${header.count} items"
        }
    }

    class NotificationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val icon: ImageView =
            itemView.findViewById(R.id.iv_notification_icon)

        private val title: TextView =
            itemView.findViewById(R.id.tv_notification_title)

        private val subtitle: TextView =
            itemView.findViewById(R.id.tv_notification_subtitle)

        private val time: TextView =
            itemView.findViewById(R.id.tv_notification_time)

        private val unreadDot: ImageView =
            itemView.findViewById(R.id.iv_unread_dot)

        fun bind(notification: NotificationItem) {

            // 🛡 Safe icon binding
            runCatching {
                icon.setImageResource(notification.iconRes)
            }

            runCatching {
                icon.background =
                    itemView.context.getDrawable(
                        notification.backgroundRes
                    )
            }

            title.text = notification.title
            subtitle.text = notification.subtitle
            time.text = notification.time

            unreadDot.visibility =
                if (notification.isUnread)
                    View.VISIBLE
                else
                    View.GONE
        }
    }
}
