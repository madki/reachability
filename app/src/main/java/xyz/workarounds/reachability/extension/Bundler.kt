package xyz.workarounds.reachability.extension

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import timber.log.Timber
import xyz.workarounds.reachability.activity.BlankActivity
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by madki on 22/11/15.
 */
public class Bundler() {
    var args: Bundle = Bundle()
    var states: Bundle = Bundle()

    constructor(intent: Intent?) : this() {
        init(intent)
    }

    constructor(bundle: Bundle?) : this() {
        init(bundle)
    }

    fun init(intent: Intent?) {
        init(intent?.extras)
    }

    fun init(bundle: Bundle?) {
        args = bundle ?: Bundle()
        states = bundle ?: Bundle()
        Timber.d("Unbundling: ${bundle?.string()}")
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> get(key: String, arg: Boolean, state: Boolean): V? {
        if (state && states.containsKey(key)) {
            return states.get(key) as V?;
        } else if (arg && args.containsKey(key)) {
            return args.get(key) as V?;
        } else {
            return null
        }
    }

    fun <V> set(key: String, value: V, arg: Boolean, state: Boolean) {
        if (state) {
            states.put(key, value)
        } else if (arg) {
            args.put(key, value)
        } else {
            throw IllegalStateException("Bundler method set called on neither state nor arg")
        }
    }

    fun saveState(bundle: Bundle): Bundle {
        bundle.putAll(states)
        return bundle;
    }

    fun restoreState(savedState: Bundle?) {
        states.putAll(savedState ?: Bundle())
    }
}

public class Delegator<T : Bundlable, V>(val key: String?, val arg: Boolean, val state: Boolean) : ReadWriteProperty<T, V?> {
    override fun getValue(thisRef: T, property: KProperty<*>): V? {
        return thisRef.bundler.get(key ?: property.name, arg, state)
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        try {
            thisRef.bundler.set(key ?: property.name, value, arg, state)
        } catch(e: IllegalArgumentException) {
            Timber.d("State ${property.name} not saved to bundle. Unrecognized type")
        }
    }
}


@Suppress("UNCHECKED_CAST")
fun <T> Bundle.put(key: String, value: T): Unit = when (value) {
    is Boolean -> putBoolean(key, value)
    is BooleanArray -> putBooleanArray(key, value)
    is Byte -> putByte(key, value)
    is ByteArray -> putByteArray(key, value)
    is Char -> putChar(key, value)
    is CharArray -> putCharArray(key, value)
    is CharSequence -> putCharSequence(key, value)
    is Double -> putDouble(key, value)
    is DoubleArray -> putDoubleArray(key, value)
    is Float -> putFloat(key, value)
    is FloatArray -> putFloatArray(key, value)
    is Int -> putInt(key, value)
    is IntArray -> putIntArray(key, value)
    is Long -> putLong(key, value)
    is LongArray -> putLongArray(key, value)
    is Parcelable -> putParcelable(key, value)
    is Short -> putShort(key, value)
    is ShortArray -> putShortArray(key, value)
    is String -> putString(key, value)
    is Serializable -> putSerializable(key, value)
    is Array<*> -> when {
        value.isArrayOf<Parcelable>() -> putParcelableArray(key, value as Array<Parcelable>)
        value.isArrayOf<String>() -> putStringArray(key, value as Array<String>)
        value.isArrayOf<CharSequence>() -> putCharSequenceArray(key, value as Array<CharSequence>)
        else -> throw IllegalArgumentException("Unsupported array type. Unable to put $key in bundle.")
    }
    else -> throw IllegalArgumentException("Unsupported type. Unable to put $key in bundle.");
}

fun Bundle.string(): String {
    var string = "Bundle{ "
    for (key in keySet()) {
        string += "$key : ${get(key)}, "
    }
    string += " }"
    return string;
}

inline fun <reified T: Activity> Context.startActivity(put: Intent.() -> Unit) {
    var intent = Intent(this, T::class.java)
    intent.put()
    startActivity(intent)
}

inline fun <reified T: Service> Context.startService(put: Intent.() -> Unit) {
    var intent = Intent(this, T::class.java)
    intent.put()
    startService(intent)
}

public fun Service.startActivityForResult(activityIntent: Intent, requestCode: Int) {
    var serviceName = javaClass.name
    startActivity<BlankActivity> {
        putExtra(BlankActivity.REQUEST_CODE, requestCode)
        putExtra(BlankActivity.ACTIVITY_INTENT, activityIntent)
        putExtra(BlankActivity.SERVICE_NAME, serviceName)
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
