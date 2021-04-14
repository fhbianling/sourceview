package com.bian.sourceviewer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle

/**
 * author fhbianling@163.com
 * date 2021/3/19 11:39
 * 类描述：
 */
class SourceCodeViewer : Activity() {
    private var orientation = Configuration.ORIENTATION_PORTRAIT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_code_viewer)
        orientation = resources.configuration.orientation
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        val key = intent.getStringExtra(KEY)
        findViewById<SourceCodeView>(R.id.sourceCodeView).loadSourceCode(key)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    companion object {
        private const val KEY = "sourceCodeKey"
        fun start(ctx: Context, sourceCodeKey: String) {
            val starter = Intent(ctx, SourceCodeViewer::class.java)
            starter.putExtra(KEY, sourceCodeKey)
            ctx.startActivity(starter)
        }
    }
}