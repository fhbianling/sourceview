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
        findViewById<View>(R.id.queryMainLayout).setOnClickListener {
            SourceBinding.mainLayout.open(this)
        }
        findViewById<View>(R.id.queryStrings).setOnClickListener {
            SourceBinding.src["src/main/res/values/strings.xml"]?.open(this)
        }
        findViewById<View>(R.id.queryColors).setOnClickListener {
            SourceBinding.src["colors.xml"]?.open(this)
        }

        findViewById<View>(R.id.querySrc).setOnClickListener {
            SourceBinding.src?.open(this)
        }
    }
}