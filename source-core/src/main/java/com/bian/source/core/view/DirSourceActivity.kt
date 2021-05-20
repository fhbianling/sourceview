package com.bian.source.core.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.bian.source.core.*
import java.util.*

/**
 * author fhbianling@163.com
 * date 2021/5/19 16:58
 * 类描述：
 */
class DirSourceActivity : Activity() {
    private val breadCrumbsStack = Stack<SourceIndex>()
    private lateinit var breadCrumbsTv: TextView
    private lateinit var childrenList: ListView
    private var root: SourceIndex? = null
    private val adapter by lazy {
        Adapter(this) {
            if (it.isDir) {
                breadCrumbsStack.push(it)
                updateUI()
            } else {
                FileSourceActivity.start(this, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dir_source)
        val path = intent.getStringExtra(KEY_PATH)
        val id = intent.getStringExtra(KEY_ID) ?: ""
        root = Source.instance.query(id)
        if (root == null || path == null) {
            findViewById<TextView>(R.id.hint).visibility = View.VISIBLE
            return
        }
        root?.initStack(path, breadCrumbsStack)
        breadCrumbsTv = findViewById(R.id.breadCrumbs)
        childrenList = findViewById(R.id.childrenList)
        breadCrumbsTv.movementMethod = LinkMovementMethod.getInstance()
        childrenList.adapter = adapter
        updateUI()
    }

    private fun updateUI() {
        breadCrumbsTv.text = breadCrumbsStack.generateSpannableString(::onStackElementClicked)
        adapter.list = breadCrumbsStack.peek().children ?: emptyList()
    }

    private fun onStackElementClicked(sourceIndex: SourceIndex?) {
        while (breadCrumbsStack.peek() != sourceIndex && !breadCrumbsStack.isEmpty()) {
            breadCrumbsStack.pop()
        }
        updateUI()
    }

    override fun onBackPressed() {
        if (breadCrumbsStack.size >= 2) {
            breadCrumbsStack.pop()
            updateUI()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val KEY_PATH = "path"
        private const val KEY_ID = "id"
        fun start(ctx: Context, sourceIndex: SourceIndex) {
            Log.d("DirSourceActivity", "start")
            val starter = Intent(ctx, DirSourceActivity::class.java)
            starter.putExtra(KEY_PATH, sourceIndex.path)
            starter.putExtra(KEY_ID, sourceIndex.rootId)
            ctx.startActivity(starter)
        }
    }
}

internal class Adapter(context: Context, private val onItemClick: (SourceIndex) -> Unit) :
    BaseAdapter() {
    var list: List<SourceIndex>? = null
        set(value) {
            field = value?.sortedBy { if (it.isDir) -1 else 1 }
            notifyDataSetChanged()
        }
    private val layoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = list?.size ?: 0

    override fun getItem(position: Int): SourceIndex? = list?.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: Holder?
        val view: View?
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_list, parent, false)
            holder = Holder(view)
            view.tag = holder
            view.setOnClickListener { v ->
                val sourceIndex = (v.tag as? Holder)?.sourceIndex
                sourceIndex?.let {
                    onItemClick.invoke(it)
                }
            }
        } else {
            view = convertView
            holder = view.tag as Holder
        }
        getItem(position)?.let {
            holder.bind(it)
        }
        return view!!
    }

    private class Holder(view: View) {
        private val tv = view as TextView
        var sourceIndex: SourceIndex? = null
        fun bind(sourceIndex: SourceIndex) {
            tv.text = sourceIndex.name
            if (sourceIndex.isDir) {
                tv.setDirIcon()
            } else {
                tv.setDirIcon(true)
            }
            this.sourceIndex = sourceIndex
        }

        private fun TextView.setDirIcon(clear: Boolean = false) {
            if (clear) {
                setCompoundDrawables(null, null, null, null)
            } else {
                val d = context.getDrawable(R.mipmap.ic_dir) ?: return
                d.setBounds(0, 0, 60, 60)
                setCompoundDrawables(d, null, null, null)
            }
        }
    }
}