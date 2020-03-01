package com.mslxl.fubuki_tsuhatsuha.ui.query

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.data.model.WorkItem
import com.mslxl.fubuki_tsuhatsuha.ui.about.AboutDialog
import com.mslxl.fubuki_tsuhatsuha.ui.answer.AnswerActivity
import com.mslxl.fubuki_tsuhatsuha.ui.query.adapter.WorkAdapter

class QueryActivity : AppCompatActivity(), WorkAdapter.OnItemClick {
    private val queryViewModel by viewModels<QueryViewModel> {
        QueryViewModelFactory(
            owner = this,
            defaultArgs = intent.extras!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        val info = findViewById<TextView>(R.id.text_student_info)
        val list = findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(this@QueryActivity)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        val about = findViewById<Button>(R.id.about)
        about.setOnClickListener {
            AboutDialog().show(this)
        }

        queryViewModel.requestUserInfoResult.observe(this) {
            if (it.success != null) {
                val model = it.success
                val infoTxt = """
                    TOKEN: ${queryViewModel.token}
                    姓名: ${model.username} (${model.ru})
                    学校: ${model.schoolName} (${model.schoolGuid})
                    班级: ${model.className} (GradeCode:${model.gradeCode};ClassCode:${model.classCode})
                """.trimIndent()
                info.text = infoTxt
            } else {
                exitDueToError(it.errorCode!!, it.msg!!)
            }

            queryViewModel.requestWork()
        }
        queryViewModel.requestWorkListResult.observe(this) {
            if (it.success != null) {
                val model = it.success
                list.adapter = WorkAdapter(model.works).apply {
                    onItemClickListener = this@QueryActivity
                    notifyDataSetChanged()
                }
            } else {
                exitDueToError(it.code!!, it.msg!!)
            }
        }


        queryViewModel.requestUserInfo()

    }

    private fun exitDueToError(code: Int, msg: String) {
        Toast.makeText(applicationContext, "$code:$msg", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onItemClick(model: WorkItem) {
        Log.d("click", "Query Answer :$model")
        Intent(this, AnswerActivity::class.java).apply {
            val userInfo = queryViewModel.requestUserInfoResult.value!!.success!!
            putExtra("ru", userInfo.ru)
            putExtra("token", queryViewModel.token)
            putExtra("guid", model.homeWorkGuid)
            putExtra("title", model.name)
        }.let {
            this.startActivity(it)
        }
    }
}
