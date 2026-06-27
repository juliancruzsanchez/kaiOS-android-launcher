package com.flipos.launcher.ui

import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flipos.launcher.R
import com.flipos.launcher.data.AppInfo
import com.flipos.launcher.data.LauncherPrefs
import com.flipos.launcher.data.NotificationDotColor

/** Grid of apps used by the app drawer. Icons only — [onFocusChanged] lets the
 * screen mirror the focused app's name in its title bar in place of labels. */
class AppGridAdapter(
    private val onClick: (AppInfo) -> Unit,
    private val onLongClick: (AppInfo) -> Unit,
    private val onFocusChanged: (AppInfo) -> Unit = {},
    private val iconSizePercent: Int = LauncherPrefs.DEFAULT_ICON_SIZE_PERCENT,
    private val hasNotification: (AppInfo) -> Boolean = { false },
) : RecyclerView.Adapter<AppGridAdapter.VH>() {

    private val items = ArrayList<AppInfo>()

    fun submit(list: List<AppInfo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun itemAt(position: Int): AppInfo? = items.getOrNull(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_grid, parent, false)
        return VH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val notifDot: View = itemView.findViewById(R.id.notif_dot)

        fun bind(app: AppInfo) {
            val px = iconPx()
            icon.layoutParams = icon.layoutParams.apply { width = px; height = px }
            icon.setImageDrawable(app.icon)
            icon.contentDescription = app.label
            if (hasNotification(app)) {
                notifDot.visibility = View.VISIBLE
                notifDot.backgroundTintList = ColorStateList.valueOf(NotificationDotColor.forIcon(app.key, app.icon))
            } else {
                notifDot.visibility = View.GONE
            }
            itemView.setOnClickListener { onClick(app) }
            itemView.setOnLongClickListener {
                onLongClick(app)
                true
            }
            itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) onFocusChanged(app)
            }
        }

        /** [iconSizePercent] of the grid's current column width, after
         * item_app_grid's 4dp margin + 8dp padding on each side - sized off the
         * actual cell, not a fixed dp value, so it can never overflow past the
         * FrameLayout and get clipped, regardless of screen width. */
        private fun iconPx(): Int {
            val rv = itemView.parent as? RecyclerView ?: return fallbackIconPx()
            val columns = (rv.layoutManager as? GridLayoutManager)?.spanCount ?: return fallbackIconPx()
            if (rv.width <= 0 || columns <= 0) return fallbackIconPx()
            val density = Resources.getSystem().displayMetrics.density
            val cellWidth = (rv.width - rv.paddingStart - rv.paddingEnd) / columns
            val overheadPx = (ITEM_OVERHEAD_DP * density).toInt()
            val available = (cellWidth - overheadPx).coerceAtLeast(0)
            return available * iconSizePercent / 100
        }

        private fun fallbackIconPx(): Int = itemView.resources.getDimensionPixelSize(R.dimen.app_icon)
    }

    companion object {
        /** item_app_grid's 4dp margin + 8dp padding on each side. */
        const val ITEM_OVERHEAD_DP = 24
    }
}
