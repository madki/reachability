package xyz.workarounds.reachability.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import xyz.workarounds.reachability.R

/**
 * Created by madki on 22/11/15.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.hello).setOnClickListener {
            var second = Intent(this, SecondActivity::class.java)
            second.putExtra("first", "first")
            this.startActivity(second)
        }
    }

}