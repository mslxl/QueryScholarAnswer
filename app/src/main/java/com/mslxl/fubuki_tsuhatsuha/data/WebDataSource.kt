package com.mslxl.fubuki_tsuhatsuha.data

import android.util.Log
import com.mslxl.fubuki_tsuhatsuha.data.model.*
import com.mslxl.fubuki_tsuhatsuha.data.util.buildOkHttp
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
internal object WebDataSource {

    private fun String.api(): String {
        return "https://homework.7net.cc/api$this"
    }

    // Return sms token
    fun sendSms(phone: String): Result<String> {
        return buildOkHttp<Result<String>> {
            val content = "phone=$phone"
            val FORM = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()
            val body = content.toRequestBody(FORM)
            url("/Common/sendSMS".api())
            post(body)
        }.onError {
            return@onError Result.Error(-1, "网络连接错误: $it")
        }.onSuccess {
            val json = JSONObject(it)
            val status = json.getInt("status")
            Log.d("webdata", it)
            return@onSuccess if (status == 200) {
                Result.Success(json.getJSONObject("data").getString("token")!!)
            } else {
                Result.Error(status, json.getString("message"))
            }
        }.exec()
    }

    fun getUserInfo(token: String): Result<UserInfo> {
        return buildOkHttp<Result<UserInfo>> {
            url("/User/UserInfo".api())
            header("Token", token)
            get()
        }.onError {
            return@onError Result.Error(-1, "网络连接错误: $it")
        }.onSuccess {
            Log.d("webdata", it)
            val json = JSONObject(it)
            val status = json.getInt("status")
            val msg = json.getString("message")
            if (status == 200) {
                val data = json.getJSONObject("data")
                val username = data.getString("userName")
                val cityCode = data.getString("cityCode")
                val ru = data.getString("ru")
                val schoolGuid = data.getString("schoolGuid")
                val schoolName = data.getString("schoolName")
                val gradeCode = data.getString("gradeCode")
                val classCode = data.getString("classCode")
                val className = data.getString("className")
                val homework = data.getBoolean("homework")
                return@onSuccess Result.Success(
                    UserInfo(
                        username,
                        cityCode,
                        ru,
                        schoolGuid,
                        schoolName,
                        gradeCode,
                        classCode,
                        className,
                        homework
                    )
                )
            } else {
                return@onSuccess Result.Error(status, msg)
            }
        }.exec()
    }

    fun getWorkList(ru: String, token: String): Result<WorkList> {
        return buildOkHttp<Result<WorkList>> {
            url("/Student/GetWorkList?rucode=$ru&pageIndex=0&pageLength=10".api())
            header("Token", token)
            get()
        }.onError {
            return@onError Result.Error(-1, "网络连接失败: $it")
        }.onSuccess {
            val json = JSONObject(it)
            Log.d("webdata", it)
            val status = json.getInt("status")
            val msg = json.getString("message")
            if (status == 200) {
                val data = json.getJSONObject("data")
                val studentGuid = data.getString("studentGuid")
                val list = data.getJSONArray("list")
                val workList = ArrayList<WorkItem>(10)
                for (i in 0 until list.length()) {
                    val dayWork = list.getJSONObject(i).getJSONArray("data")
                    for (j in 0 until dayWork.length()) {

                        fun JSONObject.hasGetInt(key: String, value: Int = Int.MIN_VALUE): Int {
                            return if (has(key)) getInt(key) else value
                        }

                        val d = dayWork.getJSONObject(j)
                        val name = d.getString("name")
                        val homeWorkGuid = d.getString("homeWorkGuid")
                        val endReleaseTime = d.getString("endReleaseTime")
                        val subject = d.getString("subject")

                        val days = d.hasGetInt("days")
                        val state = d.hasGetInt("state")
                        val hours = d.hasGetInt("hours")
                        val isOverdue = d.hasGetInt("isOverdue") != 0
                        val work = WorkItem(
                            name,
                            homeWorkGuid,
                            endReleaseTime,
                            subject,
                            state,
                            days,
                            hours,
                            isOverdue
                        )
                        workList.add(work)
                    }
                }
                return@onSuccess Result.Success(data = WorkList(studentGuid, workList))
            } else {
                return@onSuccess Result.Error(message = msg, status = status)
            }
        }.exec()
    }

    fun getAnswer(ru: String, workGuid: String, token: String): Result<Answer> {
        return buildOkHttp<Result<Answer>> {
            url("/api/Student/GetWorkAnswer?rucode=$ru&workInfoGuid=$workGuid".api())
            header("Token", token)
            get()
        }.onError {
            return@onError Result.Error(-1, "网络连接失败: $it")
        }.onSuccess { respond ->
            val json = JSONObject(respond)
            Log.d("webdata", respond)
            val status = json.getInt("status")
            if (status == 200) {

                fun <T> JSONArray.toList(block: JSONArray.(pos: Int) -> T): List<T> {
                    val list = ArrayList<T>(length())
                    for (i in 0 until length()) {
                        list.add(block.invoke(this, i))
                    }
                    return list
                }

                val data = json.getJSONObject("data")
                val objAnswer = data.getJSONArray("objAnswers")
                    .toList { getJSONObject(it).getString("answer").first() }
                val subAnswer = data.getJSONArray("subAnswers").toList { getString(it) }

                return@onSuccess Result.Success(
                    data = Answer(
                        guid = workGuid,
                        choice = objAnswer,
                        subAnswer = subAnswer
                    )
                )
            } else {
                val msg = json.getString("message")
                return@onSuccess Result.Error(message = msg, status = status)
            }
        }.exec()
    }

    fun login(phone: String, token: String, verifyCode: String): Result<User> {
        return buildOkHttp<Result<User>> {
            val FORM = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()
            val body = "phone=$phone&token=$token&verifyCode=$verifyCode".toRequestBody(FORM)
            url("/Common/Login".api())
            post(body)
        }.onError {
            return@onError Result.Error(-1, "网络连接失败: $it")
        }.onSuccess {
            val json = JSONObject(it)
            Log.d("webdata", it)
            val status = json.getInt("status")

            return@onSuccess if (status == 200) {
                val loggedToken = json.getJSONObject("data").getString("token")
                Result.Success(User(loggedToken))
            } else {
                val msg = json.getString("message")
                Result.Error(status, msg)
            }
        }.exec()



    }
}

