package com.bian.sourceviewer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout

/**
 * author fhbianling@163.com
 * date 2021/3/19 11:39
 * 类描述：
 */
class SourceCodeViewer : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentRoot = LinearLayout(this)
        contentRoot.setBackgroundColor(Color.GREEN)
        contentRoot.setPadding(50, 50, 50, 50)
        val sourceCodeViewer = SourceCodeView(this)
        contentRoot.addView(sourceCodeViewer, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        setContentView(contentRoot, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        val key = intent.getStringExtra(KEY)
        sourceCodeViewer.loadSourceCode(key)
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