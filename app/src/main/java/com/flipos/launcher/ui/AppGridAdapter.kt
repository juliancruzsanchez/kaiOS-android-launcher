package com.flipos.launcher.ui

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flipos.launcher.R
import com.flipos.launcher.data.AppInfo

/** Grid of apps used by the app drawer. Icons only — [onFocusChanged] lets the
 * screen mirror the focused app's name in its title bar in place of labels. */
class AppGridAdapter(
    private val onClick: (AppInfo) -> Unit,
    private val onLongClick: (AppInfo) -> Unit,
    private val onFocusChanged: (AppInfo) -> Unit = {},
    private val iconSizeDp: Int = 61,
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

        fun bind(app: AppInfo) {
            val px = (iconSizeDp * Resources.getSystem().displayMetrics.density).toInt()
            icon.layoutParams = icon.layoutParams.apply { width = px; height = px }
            icon.setImageDrawable(app.icon)
            icon.contentDescription = app.label
            itemView.setOnClickListener { onClick(app) }
            itemView.setOnLongClickListener {
                onLongClick(app)
                true
            }
            itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) onFocusChanged(app)
            }
        }
    }
}
