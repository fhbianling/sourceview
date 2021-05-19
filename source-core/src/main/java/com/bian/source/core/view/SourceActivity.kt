package com.bian.source.core.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.WebView
import android.widget.ListView
import android.widget.TextView
import com.bian.source.core.R
import com.bian.source.core.SourceIndex
import java.lang.StringBuilder
import java.util.*

/**
 * author fhbianling@163.com
 * date 2021/5/19 16:58
 * 类描述：
 */
class SourceActivity : Activity() {
    private val breadCrumbsStack = Stack<SourceIndex>()
    private lateinit var breadCrumbsTv: TextView
    private lateinit var childrenList: ListView
    private lateinit var sourceViewer: SourceViewer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.source_activity)
        val sourceIndex = intent.getParcelableExtra<SourceIndex>(KEY)
        if (sourceIndex == null) {
            findViewById<TextView>(R.id.hint).visibility = View.VISIBLE
            return
        }
        breadCrumbsTv = findViewById(R.id.breadCrumbs)
        childrenList = findViewById(R.id.childrenList)
        sourceViewer = findViewById(R.id.sourceViewer)
        breadCrumbsStack.push(sourceIndex)
        updateUI()
    }

    private fun updateUI() {
        val breadCrumbsText = getBreadCrumbsText()
        breadCrumbsTv.text = breadCrumbsText
    }

    private fun getBreadCrumbsText(): SpannableString {
        val string = StringBuilder()
        val indexCache = mutableListOf<Triple<Int, Int, Int>>()
        var start = 0
        for (i in 0 until breadCrumbsStack.size) {
            val sourceIndex = breadCrumbsStack[i]
            val suffix = if (i == breadCrumbsStack.size - 1) "" else "/"
            val text = "${sourceIndex.name}$suffix"
            val end = start + text.length
            indexCache.add(Triple(start, end, i))
            start = end
            string.append(text)
        }
        val spannable = SpannableString(string.toString())
        indexCache.forEach { (start, end, stackIndex) ->
            spannable.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        while (breadCrumbsStack.size - 1 != stackIndex && !breadCrumbsStack.isEmpty()) {
                            breadCrumbsStack.pop()
                        }
                        updateUI()
                    }
                }, start, end, SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        return spannable
    }

    companion object {
        private const val KEY = "sourceIndex"
        fun start(ctx: Context, sourceIndex: SourceIndex) {
            val starter = Intent(ctx, SourceActivity::class.java)
            starter.putExtra(KEY, sourceIndex)
            ctx.startActivity(starter)
        }
    }
}