package com.mslxl.fubuki_tsuhatsuha.ui.answer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.ui.answer.adapter.AnswerAdapter

class AnswerActivity : AppCompatActivity() {
    private val answerViewModel by viewModels<AnswerViewModel> {
        AnswerViewModelFactory(
            owner = this,
            defaultArgs = intent.extras!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        val list = findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(this@AnswerActivity)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }


        answerViewModel.requestAnswerResult.observe(this) { it ->
            if (it.success != null) {
                val answer = it.success
                val data = answer.choice
                    .toMutableList<Any>()
                    .apply {
                        addAll(answer.subAnswer)
                    }
                list.adapter = AnswerAdapter(data).apply {
                    notifyDataSetChanged()
                }
            } else {
                exitDueToError(it.status!!, it.msg!!)
            }
        }
        this.title = intent?.extras?.getString("title")

        answerViewModel.requestAnswer()
    }

    private fun exitDueToError(code: Int, msg: String) {
        Toast.makeText(applicationContext, "$code:$msg", Toast.LENGTH_SHORT).show()
        finish()
    }
}
