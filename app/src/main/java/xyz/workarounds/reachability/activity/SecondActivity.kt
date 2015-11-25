package xyz.workarounds.reachability.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import timber.log.Timber
import xyz.workarounds.reachability.R
import xyz.workarounds.reachability.extension.Bundlable
import xyz.workarounds.reachability.extension.Bundler
import xyz.workarounds.reachability.extension.string

/**
 * Created by madki on 22/11/15.
 */
class SecondActivity : Activity(), Bundlable {
    override var bundler: Bundler = Bundler()
    var first: String? by arg()
    var second: String? by both()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        bundler.init(intent)
        bundler.restoreState(savedInstanceState)

        var firstTv = findViewById(R.id.first) as TextView
        var secondTv = findViewById(R.id.second) as TextView
        findViewById(R.id.setSecond).setOnClickListener {
            second = "Second"
        }

        firstTv.text = first;
        secondTv.text = second;
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
    }
}