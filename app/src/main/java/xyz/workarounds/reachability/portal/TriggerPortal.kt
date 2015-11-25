package xyz.workarounds.reachability.portal

import android.content.Context
import android.view.MotionEvent
import android.view.View
import timber.log.Timber
import xyz.workarounds.reachability.R
import xyz.workarounds.reachability.service.ReachService

/**
 * Created by madki on 23/11/15.
 */
class TriggerPortal(base: Context, service: ReachService) : Portal(base, service) {

    override fun onCreate() {
        setContentView(R.layout.portal_trigger)

        findViewById(R.id.trigger)!!.setOnClickListener { service.onTrigger() }
    }
}