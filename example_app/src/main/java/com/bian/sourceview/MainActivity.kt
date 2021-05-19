package com.bian.sourceview

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bian.source.core.Source

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Source.init(this)
        val tv = findViewById<TextView>(R.id.text)
        findViewById<View>(R.id.queryContent).setOnClickListener {
            tv.text = SourceBinding.mainLayout.contentToString()
        }
        findViewById<View>(R.id.queryChildByPath).setOnClickListener {
            tv.text = SourceBinding.src["src/main/res/values/strings.xml"]?.contentToString()
        }
        findViewById<View>(R.id.queryChildByName).setOnClickListener {
            tv.text = SourceBinding.src["colors.xml"]?.contentToString()

        }
    }
}