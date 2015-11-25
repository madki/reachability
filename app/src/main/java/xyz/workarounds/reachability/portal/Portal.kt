package xyz.workarounds.reachability.portal

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.PixelFormat
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import timber.log.Timber
import xyz.workarounds.reachability.extension.append
import xyz.workarounds.reachability.extension.startActivityForResult
import xyz.workarounds.reachability.service.ReachService

/**
 * Created by madki on 22/11/15.
 */
abstract class Portal(base: Context, protected val service: ReachService) : ContextWrapper(base) {
    protected val windowManager: WindowManager
    var view: View? = null
        private set

    init {
        Timber.d("Portal created: ${javaClass.canonicalName}")
        this.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        onCreate()
    }

    open fun attach() {
        if (!isAttached && view != null) {
            windowManager.addView(view, layoutParams)
            onViewAttached()
        } else if (view == null) {
            Timber.e("View is null. Unable to attach()")
        } else {
            Timber.w("Portal already attached, ignoring attach()")
        }
    }

    open fun detach() {
        if (isAttached) {
            windowManager.removeView(view)
            onDetachView()
        } else {
            Timber.w("Portal is not attached, ignoring detach()")
        }
    }

    open fun close() {
        detach()
        onDestroy()
    }

    protected val layoutParams: WindowManager.LayoutParams
        get() {
            if (view!!.layoutParams is WindowManager.LayoutParams) {
                return view!!.layoutParams as WindowManager.LayoutParams
            } else {
                val params = WindowManager.LayoutParams()
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                params.format = PixelFormat.TRANSLUCENT

                val viewParams = view!!.layoutParams as FrameLayout.LayoutParams

                params.append(viewParams)
                return params
            }
        }

    protected abstract fun onCreate()

    protected open fun onViewAttached() {

    }

    protected open fun onDetachView() {

    }

    protected open fun onDestroy() {

    }

    fun startActivityForResult(intent: Intent, requestCode: Int) {
        service.startActivityForResult(intent, requestCode)
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, activityResult: Intent?) {

    }

    val isAttached: Boolean
        get() = view != null && view!!.isAttachedToWindow

    protected fun setContentView(view: View) {
        this.view = view
    }

    protected fun setContentView(@LayoutRes layoutId: Int) {
        setContentView(LayoutInflater.from(this).cloneInContext(this).inflate(layoutId, FrameLayout(this), false))
    }

    fun findViewById(@IdRes id: Int): View? {
        return view?.findViewById(id)
    }
}
