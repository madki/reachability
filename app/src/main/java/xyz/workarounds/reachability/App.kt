package xyz.workarounds.reachability

import android.app.Application
import android.os.Build
import timber.log.Timber

/**
 * Created by madki on 22/11/15.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}