package xyz.workarounds.reachability.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import timber.log.Timber
import xyz.workarounds.reachability.extension.Bundlable
import xyz.workarounds.reachability.extension.Bundler
import xyz.workarounds.reachability.extension.click
import xyz.workarounds.reachability.extension.longClick
import xyz.workarounds.reachability.portal.SurfacePortal
import xyz.workarounds.reachability.portal.TestPortal
import xyz.workarounds.reachability.portal.TriggerPortal

/**
 * Created by madki on 22/11/15.
 */
class ReachService : AccessibilityService(), Bundlable {
    val debug = false

    override var bundler = Bundler()
    lateinit var triggerPortal: TriggerPortal
    var surfacePortal: SurfacePortal? = null
    var testPortal: TestPortal? = null

    var intentType: Int? by arg(ForegroundService.INTENT_TYPE)
    var requestCode: Int? by arg(ForegroundService.REQUEST_CODE)
    var resultCode: Int? by arg(ForegroundService.RESULT_CODE)
    var activityResult: Intent? by arg(ForegroundService.ACTIVITY_RESULT)

    var isRootNodeDirty = false
    private var rootNode: AccessibilityNodeInfo? = null

    override fun onCreate() {
        super.onCreate()

        triggerPortal = TriggerPortal(this, this)

        if(debug) {
            testPortal = TestPortal(this, this)
        } else {
            surfacePortal = SurfacePortal(this, this)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        bundler.init(intent)
        when (intentType) {
            ForegroundService.TYPE_ACTIVITY_RESULT -> onActivityResult(requestCode!!, resultCode!!, activityResult)
            else -> Timber.d("Unhandled intentType in ReachService#onStartCommand: $intentType")
        }

        return START_STICKY
    }

    fun onTrigger() {
        surfacePortal?.onTrigger()
        testPortal?.onTrigger()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        triggerPortal.attach()
    }

    override fun onInterrupt() {
        Timber.d("onInterrupt called")
    }

    override fun onDestroy() {
        super.onDestroy()
        triggerPortal.close()
        surfacePortal?.close()
        testPortal?.close()
    }

    override fun getRootInActiveWindow(): AccessibilityNodeInfo? {
//        if (isRootNodeDirty || rootNode == null) {
            rootNode = super.getRootInActiveWindow()
//            isRootNodeDirty = false
//        }
        return if (rootNode == null) null else AccessibilityNodeInfo.obtain(rootNode)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                || event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            isRootNodeDirty == true
        }

        // TODO parse the AccessibilityNode Tree and get view bounds
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, activityResult: Intent?) {
        if (requestCode == SurfacePortal.REQUEST_CODE_MEDIA_PROJECTION) {
            surfacePortal!!.onActivityResult(requestCode, resultCode, activityResult)
        } else {
            Timber.d("Unhandled intent in ReachService#onActivityResult")
        }
    }

    fun onClickAt(x: Float, y: Float) {
        Timber.d("click@rootNode")
        if(!(rootInActiveWindow?.click(x.toInt(), y.toInt()) ?: false)) {
            Toast.makeText(this, "Unable to click. Sorry!", Toast.LENGTH_LONG).show()
        }
    }

    fun onLongClickAt(x: Float, y:Float) {
        Timber.d("longClick@rootNode")
        if(!(rootInActiveWindow?.longClick(x.toInt(), y.toInt()) ?: false)) {
            Toast.makeText(this, "Unable to LongClick. Sorry!", Toast.LENGTH_LONG).show()
        }
    }

}