package com.bian.sourceview

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.text)
        findViewById<View>(R.id.test).setOnClickListener {
            tv.text = SourceBinding.mainLayout.queryContent(this)?.let { String(it) } ?: ""
        }
        findViewById<View>(R.id.test1).setOnClickListener {
            tv.text =
                SourceBinding.src.queryChildContent("src\\main\\java\\com\\bian\\sourceview\\MainActivity.kt")
                    ?.let { String(it) } ?: ""
        }
    }
}