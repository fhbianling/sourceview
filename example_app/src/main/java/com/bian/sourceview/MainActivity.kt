package com.bian.sourceview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bian.source.core.Source

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Source.init(this)
        findViewById<View>(R.id.queryDir).setOnClickListener {
            SourceBinding.dir.open(this)
        }
        findViewById<View>(R.id.queryXml).setOnClickListener {
            SourceBinding.xml.open(this)
        }
        findViewById<View>(R.id.queryMd).setOnClickListener {
            SourceBinding.markdwon.open(this)
        }
        findViewById<View>(R.id.queryJson).setOnClickListener {
            SourceBinding.json.open(this)
        }
        findViewById<View>(R.id.queryHtml).setOnClickListener {
            SourceBinding.html.open(this)
        }
        findViewById<View>(R.id.queryKt).setOnClickListener {
            SourceBinding.kotlin.open(this)
        }
        findViewById<View>(R.id.queryJava).setOnClickListener {
            SourceBinding.java.open(this)
        }
    }
}