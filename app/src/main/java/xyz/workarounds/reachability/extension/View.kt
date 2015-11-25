package xyz.workarounds.reachability.extension

import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * Created by madki on 24/11/15.
 */

fun View.enter() {
    alpha = 0f
    translationY = -height.toFloat()
    visibility = View.VISIBLE
    animate()
            .alpha(1f)
            .withLayer()
            .translationY(0f)
            .setDuration(350)
            .setInterpolator(DecelerateInterpolator(3f))
            .start()
}

fun View.exit(endAction: () -> Unit) {
    animate()
            .alpha(0f)
            .withLayer()
            .setDuration(350)
            .setInterpolator(DecelerateInterpolator(3f))
            .withEndAction {
                visibility == View.GONE
                endAction()
            }
            .start()
}