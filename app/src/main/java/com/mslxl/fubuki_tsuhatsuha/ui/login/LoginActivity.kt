package com.mslxl.fubuki_tsuhatsuha.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.ui.about.AboutDialog
import com.mslxl.fubuki_tsuhatsuha.ui.query.QueryActivity
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels(
        factoryProducer = { LoginViewModelFactory(this.applicationContext) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val phone = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val sms = findViewById<Button>(R.id.sendSMS)
        val usePwd = findViewById<CheckBox>(R.id.use_password)
        val about = findViewById<Button>(R.id.about)
        about.setOnClickListener {
            AboutDialog().show(this)
        }

        usePwd.setOnClickListener {
            loginViewModel.usePasswordLogin = usePwd.isChecked
            if (usePwd.isChecked) {
                sms.visibility = View.INVISIBLE
                password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                sms.visibility = View.VISIBLE
                password.inputType = InputType.TYPE_NUMBER_VARIATION_NORMAL
            }
        }

        loginViewModel.smsCountdownState.observe(this@LoginActivity, Observer {
            if (it == 0L) {
                sms.isEnabled = loginViewModel.loginFormState.value?.isPhoneValid ?: false
                sms.text = getString(R.string.action_get_code)
            } else {
                sms.isEnabled = false
                sms.text = (it / 1000).toString()
            }
        })


        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer


            sms.isEnabled =
                loginViewModel.smsCountdownState.value == 0L && loginViewModel.loginFormState.value?.isPhoneValid ?: false
            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            if (loginState.usernameError != null) {
                phone.error = getString(loginState.usernameError)
            }

        })
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer
            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)
        })

        loginViewModel.smsResult.observe(this, Observer {
            it.onError { status, message ->
                Toast.makeText(applicationContext, "$status:${message}", Toast.LENGTH_LONG)
                    .show()
            }

        })

        phone.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    phone.text.toString()
                )
            }
            sms.setOnClickListener {
                loginViewModel.sendSMS(phone.text.toString())
            }
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    phone.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            phone.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(phone.text.toString(), password.text.toString())
            }
        }
        loginViewModel.loggedInUser.observe(this) { user ->
            user?.let {
                updateUiWithUser(LoggedInUserView(it.token))
            }
        }

        loginViewModel.allowStart.observe(this) {
            if (it.allow.not()) {
                Toast.makeText(this.applicationContext, it.msg, Toast.LENGTH_LONG).show()
                exitProcess(-1)
            }
        }

        loginViewModel.isAllowStart()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val token = model.token
        Toast.makeText(
            applicationContext,
            "$welcome $token",
            Toast.LENGTH_LONG
        ).show()
        Intent(this, QueryActivity::class.java).apply {
            putExtra("token", model.token)
        }.let {
            this.startActivity(it)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
