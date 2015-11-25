package xyz.workarounds.reachability.extension

import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by madki on 24/11/15.
 */

fun AccessibilityNodeInfo.click(x: Int, y: Int): Boolean {
    return performAction(x, y) { click() }
}

fun AccessibilityNodeInfo.click(): Boolean {
    if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)) {
        Log.d("Accessibility","click@targetNode")
        return performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }
    return false
}

fun AccessibilityNodeInfo.longClick(x: Int, y: Int): Boolean {
    return performAction(x, y) { longClick() }
}

fun AccessibilityNodeInfo.longClick(): Boolean {
    if (actionList.contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK)) {
        Log.d("Accessibility","longClick@targetNode")
        return performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
    }
    return false
}

fun AccessibilityNodeInfo.performAction(x: Int, y: Int, action: AccessibilityNodeInfo.() -> Boolean): Boolean {
    var actionPerformed = false
    var rect = Rect()
    getBoundsInScreen(rect)
    if (rect.contains(x, y)) {
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                actionPerformed = actionPerformed || getChild(i)?.click(x, y) ?: false
                if (actionPerformed) return true
            }
            return action()
        } else {
            return action()
        }
    }
    return false
}