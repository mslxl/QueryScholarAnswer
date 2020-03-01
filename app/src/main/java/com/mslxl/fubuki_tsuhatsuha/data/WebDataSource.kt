package com.mslxl.fubuki_tsuhatsuha.data

import android.util.Log
import com.mslxl.fubuki_tsuhatsuha.data.model.*
import com.mslxl.fubuki_tsuhatsuha.ui.answer.RequestAnswerResult
import com.mslxl.fubuki_tsuhatsuha.ui.query.RequestUserInfoResult
import com.mslxl.fubuki_tsuhatsuha.ui.query.RequestWorkListResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
internal object WebDataSource {
    // Return sms token
    fun sendSms(phone: String): Result<String> {
        val client = OkHttpClient()
        val content = "phone=$phone"
        val FORM = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()
        val body = content.toRequestBody(FORM)
        val request = Request.Builder()
            .url("https://homework.7net.cc/api/Common/sendSMS")
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
            )
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody)
        val status = json.getInt("status")
        Log.d("webdata", responseBody)

        return if (status == 200) {
            Result.Success(json.getJSONObject("data").getString("token")!!)
        } else {
            Result.Error(IOException(json.getString("message") + ": " + status))
        }
    }

    fun getUserInfo(token: String): RequestUserInfoResult {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://homework.7net.cc/api/User/UserInfo")
            .header("Token", token)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
            )
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody)
        val status = json.getInt("status")
        val msg = json.getString("message")
        Log.d("webdata", responseBody)
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
            return RequestUserInfoResult(
                success = UserInfo(
                    username,
                    cityCode,
                    ru,
                    schoolGuid,
                    schoolName,
                    gradeCode,
                    classCode,
                    className,
                    homework
                ), errorCode = status, msg = msg
            )
        } else {

            return RequestUserInfoResult(errorCode = status, msg = msg)
        }
    }

    fun getWorkList(ru: String, token: String): RequestWorkListResult {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://homework.7net.cc/api/Student/GetWorkList?rucode=$ru&pageIndex=0&pageLength=10")
            .header("Token", token)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
            )
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody)
        Log.d("webdata", responseBody)
        val status = json.getInt("status")

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
            return RequestWorkListResult(success = WorkList(studentGuid, workList))
        } else {
            val msg = json.getString("message")
            return RequestWorkListResult(msg = msg, code = status)
        }
    }

    fun getAnswer(ru: String, workGuid: String, token: String): RequestAnswerResult {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://homework.7net.cc/api/Student/GetWorkAnswer?rucode=$ru&workInfoGuid=$workGuid")
            .header("Token", token)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
            )
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody)
        Log.d("webdata", responseBody)
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

            return RequestAnswerResult(
                success = Answer(
                    guid = workGuid,
                    choice = objAnswer,
                    subAnswer = subAnswer
                ), status = status
            )
        } else {
            val msg = json.getString("message")
            return RequestAnswerResult(msg = msg, status = status)
        }
    }

    fun login(phone: String, token: String, verifyCode: String): Result<User> {
        val FORM = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()
        val client = OkHttpClient()
        val body = "phone=$phone&token=$token&verifyCode=$verifyCode".toRequestBody(FORM)
        val request = Request.Builder()
            .url("https://homework.7net.cc/api/Common/Login")
            .header("Token", token)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
            )
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody)
        Log.d("webdata", responseBody)
        val status = json.getInt("status")

        return if (status == 200) {
            val loggedToken = json.getJSONObject("data").getString("token")
            Result.Success(User(loggedToken))
        } else {
            val msg = json.getString("message")
            Result.Error(IOException("$status:$msg"))
        }
    }
}

