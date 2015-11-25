package xyz.workarounds.reachability.portal

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.*
import timber.log.Timber
import xyz.workarounds.reachability.R
import xyz.workarounds.reachability.activity.BlankActivity
import xyz.workarounds.reachability.extension.enter
import xyz.workarounds.reachability.extension.exit
import xyz.workarounds.reachability.service.ReachService

/**
 * Created by madki on 23/11/15.
 */
class SurfacePortal(base: Context, service: ReachService) : Portal(base, service) {

    var screenDensity = 0
    var screenHeight = 0
    var screenWidth = 0

    var resultCode = 0
    var activityResult: Intent? = null

    lateinit var surfaceView: SurfaceView
    lateinit var surface: Surface
    lateinit var container: View

    lateinit var mediaProjectionManager: MediaProjectionManager
    var mediaProjection: MediaProjection? = null
    var virtualDisplay: VirtualDisplay? = null

    var screenCaptureRequested = false

    lateinit var detector: GestureDetector

    override fun onCreate() {
        setContentView(R.layout.portal_surface)

        detector = GestureDetector(this, PrimitiveGestureListener(service))

        var metrics = DisplayMetrics();
        windowManager.defaultDisplay.getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


        surfaceView = findViewById(R.id.surface)!! as SurfaceView
        surface = surfaceView.holder.surface
        container = findViewById(R.id.container_surface)!!

        findViewById(R.id.close)!!.setOnClickListener { onTrigger() }
        surfaceView.setOnTouchListener { view, motionEvent -> onTouchEvent(view, motionEvent) }

        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun onTouchEvent(view: View, event: MotionEvent): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun attach() {
        super.attach()
        container.enter()
    }

    override fun detach() {
        container.exit { super.detach() }
    }

    override fun onViewAttached() {
        Timber.d("onViewAttached")
        startScreenCapture()
    }

    override fun onDetachView() {
        Timber.d("onDetachView")
        stopScreenCapture()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, activityResult: Intent?) {
        Timber.d("onActivityResult")
        if (requestCode == REQUEST_CODE_MEDIA_PROJECTION &&
                resultCode == BlankActivity.RESULT_OK &&
                activityResult != null) {
            this.resultCode = resultCode
            this.activityResult = activityResult

            if (screenCaptureRequested) {
                screenCaptureRequested = false
                startScreenCapture()
            }

        } else if (requestCode != REQUEST_CODE_MEDIA_PROJECTION) {
            Timber.d("Unhandled request code in surface portal $requestCode")
        } else {
            Timber.d("User denied permission for screen capture")
        }
    }

    fun onTrigger() {
        if (isAttached) detach() else attach()
    }

    private fun startScreenCapture() {
        if (mediaProjection != null) {
            setUpVirtualDisplay()
        } else if (resultCode != 0 && activityResult != null) {
            setUpMediaProjection()
            setUpVirtualDisplay()
        } else {
            screenCaptureRequested = true
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_MEDIA_PROJECTION)
        }
    }

    private fun stopScreenCapture() {
        Timber.d("stopScreenCapture")
        closeVirtualDisplay()
        stopMediaProjection()
        resultCode = 0
        activityResult = null
    }

    private fun setUpMediaProjection() {
        Timber.d("setUpMediaProjection")
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, activityResult)
    }

    private fun stopMediaProjection() {
        Timber.d("stopMediaProjection")
        mediaProjection?.stop()
        mediaProjection = null
    }

    private fun setUpVirtualDisplay() {
        Timber.d("Setting up a VirtualDisplay: $screenWidth x $screenHeight ($screenDensity)")
        virtualDisplay = mediaProjection!!.createVirtualDisplay("ScreenCapture",
                screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface, null, null);
    }

    private fun closeVirtualDisplay() {
        Timber.d("closeVirtualDisplay")
        virtualDisplay?.release()
        virtualDisplay = null
    }

    companion object {
        val REQUEST_CODE_MEDIA_PROJECTION = 1
    }

    class PrimitiveGestureListener(val service: ReachService) : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e != null) {
                Timber.d("onClick@Listener")
                service.onClickAt(e.x, e.y)
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            if (e != null) {
                Timber.d("LongClick@Listener")
                service.onLongClickAt(e.x, e.y)
            }
        }
    }

}