package com.bian.sourceviewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.webkit.*
import com.bian.sourceviewcore.tool.SourceCodeManager
import com.google.gson.Gson

/**
 * author fhbianling@163.com
 * date 2021/3/19 11:40
 * 类描述：
 */
private const val BASE_URL = "file:///android_asset/sourcecodeviewer/index.html"

class SourceCodeView(ctx: Context) : WebView(ctx) {
    private var pagePrepared = false
    private var cacheSourceCodeDto: SourceCodeDto? = null

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        settings.apply {
            javaScriptEnabled = true
            setSupportZoom(true)
            displayZoomControls = false
            javaScriptCanOpenWindowsAutomatically = true
        }
        setBackgroundColor(Color.TRANSPARENT)
        background?.alpha = 0
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("SourceCodeView", "onPageFinished")
                pagePrepared = url == BASE_URL
                if (cacheSourceCodeDto != null && pagePrepared) {
                    displaySourceCode(cacheSourceCodeDto!!)
                    cacheSourceCodeDto = null
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.d("SourceCodeView", "onReceivedError")
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.d("SourceCodeView", "onReceivedHttpError")
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.d("SourceCodeView", "onReceivedSslError")
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.d("SourceCodeView", "onReceivedError")
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
        loadUrl(BASE_URL)
    }


    fun displaySourceCode(dto: SourceCodeDto) {
        if (pagePrepared) {
            val json = Gson().toJson(dto)
            loadUrl("javascript:setSourceCodeDto($json)")
        } else {
            cacheSourceCodeDto = dto
        }
    }

    fun loadSourceCode(key: String?) {
        key?.let {
            val source = SourceCodeManager.instance.querySourceCode(context, key)
            Log.d("SourceCodeView", "load source code for $key is empty ? ${source.isEmpty()}")
            val dto = SourceCodeDto(key, source)
            displaySourceCode(dto)
        }
    }

    data class SourceCodeDto(val key: String, val sourceCode: String)
}