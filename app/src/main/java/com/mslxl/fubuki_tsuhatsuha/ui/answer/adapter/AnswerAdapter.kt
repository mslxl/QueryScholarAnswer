package com.mslxl.fubuki_tsuhatsuha.ui.answer.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mslxl.fubuki_tsuhatsuha.R
import java.io.File

class AnswerAdapter(val answer: List<String>) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val no: TextView
        val image: ImageView
        val choice: TextView

        init {
            with(view) {
                no = findViewById(R.id.no)
                image = findViewById(R.id.subanswer)
                choice = findViewById(R.id.choice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return ViewHolder(view).apply {
            image.setOnClickListener {
                fun File.mk() = this.apply {
                    if (this.exists().not()) {
                        Log.d("io", "Mkdirs $absolutePath")
                        mkdirs()
                    }
                }

                fun File.touch() = this.apply {
                    if (this.exists().not()) {
                        parentFile.mk()
                        Log.d("io", "Create new file $absolutePath")
                        createNewFile()
                    }
                }

                val externalStoragePublicDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val dir = File(externalStoragePublicDirectory, "answer").mk()
                val file = File(dir, System.currentTimeMillis().toString() + ".png").touch()
                file.outputStream().use {
                    image.drawable.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                Intent().apply {
                    action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                    data = Uri.fromFile(file)
                }.let {
                    image.context.sendBroadcast(it)
                }
                Toast.makeText(image.context, "文件已保存至 ${file.absolutePath}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return answer.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.no.text = (position + 1).toString()
        val item = answer[position]
        when {
            item.length <= 4 -> {
                holder.image.visibility = View.GONE
                holder.choice.visibility = View.VISIBLE
                holder.choice.text = item
            }
            else -> {
                holder.choice.visibility = View.GONE
                holder.image.visibility = View.VISIBLE
                Glide.with(holder.image).load(item).placeholder(R.drawable.loading)
                    .into(holder.image)
            }
        }
    }

}