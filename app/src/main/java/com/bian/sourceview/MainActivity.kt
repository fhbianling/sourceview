@file:ProvideSourceCode("main activity")
package com.bian.sourceview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bian.sourceviewcore.annotation.ProvideSourceCode
import com.bian.sourceviewer.SourceCodeViewer

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		findViewById<View>(R.id.test).setOnClickListener {
			SourceCodeViewer.start(this,"main activity")
		}
		findViewById<View>(R.id.test1).setOnClickListener {
			SourceCodeViewer.start(this,"test code")
		}
	}
}