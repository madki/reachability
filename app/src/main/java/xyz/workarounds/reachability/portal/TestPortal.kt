package xyz.workarounds.reachability.portal

import android.content.Context
import android.view.MotionEvent
import android.view.View
import xyz.workarounds.reachability.R
import xyz.workarounds.reachability.service.ReachService

/**
 * Created by madki on 23/11/15.
 */
class TestPortal(base: Context, service: ReachService) : Portal(base, service) {

    override fun onCreate() {
        setContentView(R.layout.portal_test)

        findViewById(R.id.close)!!.setOnClickListener { detach() }
        findViewById(R.id.container_test)!!.setOnTouchListener { view, motionEvent -> onTouchEvent(view, motionEvent) }
    }

    fun onTouchEvent(view: View, event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN) {
            return true
        }

        if(event.action == MotionEvent.ACTION_UP) {
            onClickAt(event.x, event.y)
            return true
        }

        return true
    }

    fun onClickAt(x: Float, y: Float) {
        service.onClickAt(x, y)
    }

    fun onTrigger() {
        if(!isAttached) attach() else detach()
    }

}
