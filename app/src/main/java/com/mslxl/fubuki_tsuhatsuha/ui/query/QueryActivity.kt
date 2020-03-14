package com.mslxl.fubuki_tsuhatsuha.ui.query

import android.content.*
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.data.model.WorkItem
import com.mslxl.fubuki_tsuhatsuha.data.util.md5
import com.mslxl.fubuki_tsuhatsuha.ui.about.AboutDialog
import com.mslxl.fubuki_tsuhatsuha.ui.answer.AnswerActivity
import com.mslxl.fubuki_tsuhatsuha.ui.query.adapter.WorkAdapter

class QueryActivity : AppCompatActivity(), WorkAdapter.OnItemClick {
    private val queryViewModel by viewModels<QueryViewModel> {
        QueryViewModelFactory(
            applicationContext,
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
            it.onSuccess { model ->
                val infoTxt = """
                    TOKEN: ${queryViewModel.token}
                    姓名: ${model.username} (${model.ru})
                    学校: ${model.schoolName} (${model.schoolGuid})
                    班级: ${model.className} (GradeCode:${model.gradeCode};ClassCode:${model.classCode})
                """.trimIndent()
                info.text = infoTxt
            }
            it.onError { status, message ->
                exitDueToError(status, message)
            }
            queryViewModel.requestWork()


        }
        queryViewModel.requestWorkListResult.observe(this) {
            it.onSuccess { model ->
                list.adapter = WorkAdapter(model.works).apply {
                    onItemClickListener = this@QueryActivity
                    notifyDataSetChanged()
                }
            }
            it.onError { status, message ->
                exitDueToError(status, message)
            }
        }

        queryViewModel.requestUserInfoResult.observe(this){
            it.onSuccess {
                info->

                // 二次密码验证
                // 控制传播范围

                val classId = "${info.schoolGuid}-${info.gradeCode}-${info.classCode}"
                val nameID = info.username

                val studentInfo = Base64.encodeToString("""
                    name:${info.username}
                    class: ${info.className} ($classId)
                    school:${info.schoolName} (${info.schoolGuid}})
                """.trimIndent().toByteArray(),Base64.DEFAULT)
                if(queryViewModel.secondPassword != null && queryViewModel.secondPassword in arrayOf(classId.md5(),nameID.md5())){
                    // 验证通过
                    queryViewModel.allowUse = true
                    return@observe
                }
                showPasswordDialog(studentInfo){ pwd->
                    val result = pwd in arrayOf(classId.md5(),nameID.md5())
                    queryViewModel.allowUse = result
                    return@showPasswordDialog result
                }

            }
        }


        queryViewModel.requestUserInfo()
    }

    private fun showPasswordDialog(requestCode:String,passwordCallback:(String)->Boolean){
        AlertDialog.Builder(this).apply {
            val inflater = LayoutInflater.from(applicationContext)
            val view = inflater.inflate(R.layout.dialog_second_password,null)
            val editField = view.findViewById<EditText>(R.id.editText)
            setCancelable(false)
            setView(view)
            setPositiveButton("确认") { dialog, which ->
                if(passwordCallback.invoke(editField.text.toString())){
                    Toast.makeText(this@QueryActivity,"验证通过",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    showPasswordDialog(requestCode, passwordCallback)
                    Toast.makeText(this@QueryActivity,"验证失败",Toast.LENGTH_SHORT).show()
                }
            }

            setNegativeButton("复制申请码"){ dialog, which ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("request code",requestCode)
                clipboard.primaryClip = clip
                showPasswordDialog(requestCode, passwordCallback)
                Toast.makeText(this@QueryActivity,"已复制申请码",Toast.LENGTH_SHORT).show()
            }

        }.show()
    }

    private fun exitDueToError(code: Int, msg: String) {
        Toast.makeText(applicationContext, "$code:$msg", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onItemClick(model: WorkItem) {

        if(queryViewModel.allowUse.not()){
            Toast.makeText(this@QueryActivity,"二次验证未通过",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("click", "Query Answer :$model")
        Intent(this, AnswerActivity::class.java).apply {
            queryViewModel.requestUserInfoResult.value!!.onSuccess { userInfo ->
                putExtra("ru", userInfo.ru)
                putExtra("token", queryViewModel.token)
                putExtra("guid", model.homeWorkGuid)
                putExtra("title", model.name)
            }.onError { status, message ->
                exitDueToError(status, message)
            }
        }.let {
            this.startActivity(it)
        }
    }
}
