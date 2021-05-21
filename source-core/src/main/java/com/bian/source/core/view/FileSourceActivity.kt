package com.bian.source.core.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import com.bian.source.core.R
import com.bian.source.core.Source
import com.bian.source.core.SourceIndex

/**
 * author fhbianling@163.com
 * date 2021/5/20 10:49
 * 类描述：
 */
class FileSourceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.file_source)
        val path = intent.getStringExtra(KEY_PATH) ?: ""
        val id = intent.getStringExtra(KEY_ID) ?: ""
        val root = Source.instance.query(id)
        val sourceIndex = if (root?.name == path) root else root?.queryChildByPath(path)
        sourceIndex?.let {
            findViewById<SourceViewer>(R.id.sourceViewer).displaySource(it)
        }
    }

    companion object {
        private const val KEY_PATH = "path"
        private const val KEY_ID = "id"
        fun start(ctx: Context, sourceIndex: SourceIndex) {
            Log.d("FileSourceActivity", "start:${sourceIndex}")
            val starter = Intent(ctx, FileSourceActivity::class.java)
            starter.putExtra(KEY_PATH, sourceIndex.path)
            starter.putExtra(KEY_ID, sourceIndex.rootId)
            ctx.startActivity(starter)
        }
    }
}