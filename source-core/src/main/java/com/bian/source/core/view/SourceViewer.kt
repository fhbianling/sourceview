package com.bian.source.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bian.source.core.SourceIndex
import com.bian.source.core.Type
import com.google.gson.Gson

/**
 * author fhbianling@163.com
 * date 2021/5/19 17:25
 * 类描述：
 */
private const val BASE_URL = "file:///android_asset/sourceView/index.html"

@SuppressLint("SetJavaScriptEnabled")
class SourceViewer(ctx: Context, attrs: AttributeSet) : WebView(ctx, attrs) {
    private var pagePrepared = false
    private var cachedSourceIndex: SourceIndex? = null
    private val gson = Gson()

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        with(settings) {
            javaScriptEnabled = true
            setSupportZoom(true)
            displayZoomControls = false
        }
        setBackgroundColor(Color.TRANSPARENT)
        background?.alpha = 0
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                pagePrepared = url?.equals(BASE_URL) ?: false
                if (pagePrepared) {
                    cachedSourceIndex?.let {
                        displaySource(it)
                        cachedSourceIndex = null
                    }
                }
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        loadUrl(BASE_URL)
    }

    fun displaySource(sourceIndex: SourceIndex) {
        if (pagePrepared) {
            val source = Source(
                sourceIndex.name ?: "",
                sourceIndex.type ?: Type.Text.id,
                sourceIndex.contentToString()
            )
            val json = gson.toJson(source)
            Log.d("SourceViewer", "displaySource:$json")
            loadUrl("javascript:setSourceDto($json)")
        } else {
            cachedSourceIndex = sourceIndex
        }
    }

    private data class Source(val name: String, val type: String, val content: String)
}