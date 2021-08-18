package com.bian.example_android_lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bian.source.core.view.SourceViewer

/**
 * author fhbianling@163.com
 * date 2021/8/18 10:51
 * 类描述：
 */
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        findViewById<SourceViewer>(R.id.sc).displaySource(SourceBinding.test)
    }
}