package xyz.workarounds.reachability.extension

import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Created by madki on 23/11/15.
 */

fun WindowManager.LayoutParams.append(flParams: FrameLayout.LayoutParams) {
    gravity = flParams.gravity;
    height = flParams.height;
    width = flParams.width;
    if (gravity.isGravityRight()) {
        x = flParams.rightMargin;
    } else {
        x = flParams.leftMargin;
    }

    if (gravity.isGravityBottom()) {
        y = flParams.bottomMargin;
    } else {
        y = flParams.topMargin;
    }
}

private fun Int.isGravityRight(): Boolean = (this and Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT

private fun Int.isGravityBottom(): Boolean = (this and Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM