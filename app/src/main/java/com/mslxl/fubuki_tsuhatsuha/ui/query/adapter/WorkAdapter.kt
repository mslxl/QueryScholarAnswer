package com.mslxl.fubuki_tsuhatsuha.ui.query.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mslxl.fubuki_tsuhatsuha.R

import com.mslxl.fubuki_tsuhatsuha.data.model.WorkItem

class WorkAdapter(val works: List<WorkItem>) : RecyclerView.Adapter<WorkAdapter.ViewHolder>() {
    var onItemClickListener: OnItemClick? = null

    class ViewHolder(val view: View, private val adapter: WorkAdapter) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView: TextView

        init {

            with(view) {
                textView = findViewById(R.id.work_info)
                setOnClickListener(this@ViewHolder)
            }

        }

        override fun onClick(p0: View?) {
            adapter.onClick(adapterPosition)
        }
    }

    interface OnItemClick {
        fun onItemClick(model: WorkItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_work, parent, false)
        return ViewHolder(view, this)

    }

    fun onClick(position: Int) {
        onItemClickListener?.onItemClick(works[position])
    }

    override fun getItemCount(): Int = works.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = works[position]
        val workTxt = """
            ${item.subject}: ${item.name}
             到期时间: ${item.endReleaseTime}
             Guid: ${item.homeWorkGuid}
        """.trimIndent()
        holder.textView.text = workTxt
    }
}