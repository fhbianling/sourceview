package com.bian.sourceviewcore.viewer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * author fhbianling@163.com
 * date 2021/3/19 11:39
 * 类描述：
 */
class SourceCodeViewer : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sourceCodeViewer = SourceCodeView(this)
        setContentView(sourceCodeViewer)
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