package xyz.workarounds.reachability.extension

import android.os.Bundle

/**
 * Created by madki on 22/11/15.
 */
interface Bundlable {
    var bundler: Bundler

    fun <T : Bundlable, V> arg(key: String? = null): Delegator<T, V> {
        return Delegator(key, true, false)
    }

    fun <T : Bundlable, V> state(key: String? = null): Delegator<T, V> {
        return Delegator(key, false, true)
    }

    fun <T : Bundlable, V> both(key: String? = null): Delegator<T, V> {
        return Delegator(key, true, true)
    }

    fun saveState(bundle: Bundle): Bundle {
        return bundler.saveState(bundle)
    }

    fun restoreState(bundle: Bundle?) {
        bundler.restoreState(bundle?: Bundle())
    }

}