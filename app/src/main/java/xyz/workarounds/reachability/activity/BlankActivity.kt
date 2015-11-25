package xyz.workarounds.reachability.activity

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import xyz.workarounds.reachability.service.ReachService
import xyz.workarounds.reachability.extension.Bundlable
import xyz.workarounds.reachability.extension.Bundler
import xyz.workarounds.reachability.extension.startService
import xyz.workarounds.reachability.service.ForegroundService

/**
 * Created by madki on 22/11/15.
 */
class BlankActivity : Activity(), Bundlable {
    override var bundler: Bundler = Bundler()
    var firstTime = true
    var resultReceived = false

    var serviceName: String? by both(SERVICE_NAME)
    var requestCode: Int? by both(REQUEST_CODE)
    var activityIntent: Intent? by both(ACTIVITY_INTENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundler.init(intent)
        restoreState(savedInstanceState)

        startActivityForResult(activityIntent!!, requestCode!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!firstTime) {
            finish()
        } else {
            firstTime = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!resultReceived) {
            sendToService(requestCode!!, RESULT_DESTROYED, null)
        }
    }

    private fun sendToService(requestCode: Int, resultCode: Int, activityResult: Intent?) {
        Timber.d("Sending result to service")
        when (serviceName) {
            ReachService::class.java.name -> {
                startService<ReachService> {
                    putExtra(ForegroundService.INTENT_TYPE, ForegroundService.TYPE_ACTIVITY_RESULT)
                    putExtra(ForegroundService.REQUEST_CODE, requestCode)
                    putExtra(ForegroundService.RESULT_CODE, resultCode)
                    putExtra(ForegroundService.ACTIVITY_RESULT, activityResult)
                }
            }
            else -> {
                Timber.d("Unhandle service delivery in BlankActivity#sendToService : $serviceName")
            }
        // TODO implement sending to service
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultReceived = true
        sendToService(requestCode, resultCode, data)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        val RESULT_OK = Activity.RESULT_OK;
        val RESULT_CANCELLED = Activity.RESULT_CANCELED;
        val RESULT_DESTROYED = -2;

        val SERVICE_NAME = "serviceName"
        val REQUEST_CODE = "requestCode"
        val ACTIVITY_INTENT = "activityIntent"
    }
}

