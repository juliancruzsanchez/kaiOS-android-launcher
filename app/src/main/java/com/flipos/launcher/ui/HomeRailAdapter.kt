package com.flipos.launcher.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flipos.launcher.R
import com.flipos.launcher.data.AppInfo

/** A rail entry: an app shortcut, or the trailing "add" tile when [app] is null. */
data class RailItem(val app: AppInfo?)

/**
 * The left home rail: stacked white app discs, plus a trailing dashed "+" tile
 * for adding another shortcut. Icons only — the position (top to bottom) is the
 * 1-9 number-key mapping.
 */
class HomeRailAdapter(
    private val onClick: (position: Int, item: RailItem) -> Unit,
    private val onLongClick: (position: Int, item: RailItem) -> Unit,
) : RecyclerView.Adapter<HomeRailAdapter.VH>() {

    private val items = ArrayList<RailItem>()

    fun submit(list: List<RailItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_rail, parent, false)
        return VH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val circle: FrameLayout = itemView.findViewById(R.id.circle)
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: RailItem) {
            val app = item.app
            if (app != null) {
                // The icon is already shaped (and given a background, if any)
                // by IconShapeRenderer when it was resolved, so it's shown as-is.
                circle.background = null
                icon.scaleType = ImageView.ScaleType.FIT_CENTER
                icon.clearColorFilter()
                icon.setImageDrawable(app.icon)
            } else {
                circle.setBackgroundResource(R.drawable.bg_circle_add)
                icon.scaleType = ImageView.ScaleType.FIT_CENTER
                icon.setImageResource(R.drawable.ic_add)
                icon.setColorFilter(Color.WHITE)
            }
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(pos, item)
            }
            itemView.setOnLongClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onLongClick(pos, item)
                true
            }
        }
    }
}
