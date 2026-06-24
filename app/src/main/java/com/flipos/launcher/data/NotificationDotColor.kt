package com.flipos.launcher.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette

/**
 * Picks the notification dot color for an app icon from its own artwork
 * (vibrant swatch, falling back to dominant) rather than a fixed launcher
 * color, so the dot always reads as "belonging" to that icon. Palette
 * generation is too slow to redo on every RecyclerView bind, so results are
 * memoized by app key.
 */
object NotificationDotColor {

    private const val SAMPLE_SIZE = 48
    private val cache = HashMap<String, Int>()

    fun forIcon(appKey: String, icon: Drawable): Int = cache.getOrPut(appKey) {
        val sample = Bitmap.createBitmap(SAMPLE_SIZE, SAMPLE_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(sample)
        icon.setBounds(0, 0, SAMPLE_SIZE, SAMPLE_SIZE)
        icon.draw(canvas)
        val palette = Palette.from(sample).generate()
        palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb ?: Color.RED
    }
}
