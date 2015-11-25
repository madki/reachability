package xyz.workarounds.reachability.service

import android.app.Service
import android.content.Intent
import xyz.workarounds.reachability.activity.BlankActivity
import xyz.workarounds.reachability.extension.startService

/**
 * Created by madki on 22/11/15.
 */
public object ForegroundService {
    val TYPE_ACTIVITY_RESULT = 1;

    val INTENT_TYPE = "intentType"
    val REQUEST_CODE = "requestCode"
    val RESULT_CODE = "resultCode"
    val ACTIVITY_RESULT = "activityResult"

}