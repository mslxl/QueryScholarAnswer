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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.data.model.User
import com.mslxl.fubuki_tsuhatsuha.ui.about.AboutDialog
import com.mslxl.fubuki_tsuhatsuha.ui.query.QueryActivity
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels(
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
        val useCode = findViewById<Switch>(R.id.use_code)
        val savePassword = findViewById<Switch>(R.id.save_password)
        val about = findViewById<Button>(R.id.about)

        savePassword.isChecked = viewModel.isSavePasswordEnable
        useCode.isChecked = viewModel.useVerifyCode.value!!
        phone.setText(viewModel.savedPhone)

        if (!useCode.isChecked && savePassword.isChecked && viewModel.savedPassword.isNotBlank()) {
            password.setText(viewModel.savedPassword)
        }

        viewModel.smsCountdownState.observe(this@LoginActivity, Observer {
            if (it == 0L) {
                sms.isEnabled = viewModel.loginFormState.value?.isPhoneValid ?: false
                sms.text = getString(R.string.action_get_code)
            } else {
                sms.isEnabled = false
                sms.text = (it / 1000).toString()
            }
        })

        viewModel.useVerifyCode.observe(this@LoginActivity, Observer {
            if (!it) {
                sms.visibility = View.GONE
                password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                password.hint = getString(R.string.prompt_password)
            } else {
                sms.visibility = View.VISIBLE
                password.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                password.hint = getString(R.string.prompt_verify_code)
            }
            savePassword.visibility = if(it) View.INVISIBLE else View.VISIBLE
        })


        viewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            sms.isEnabled =
                viewModel.smsCountdownState.value == 0L && viewModel.loginFormState.value?.isPhoneValid ?: false
            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            if (loginState.usernameError != null) {
                phone.error = getString(loginState.usernameError)
            }

        })


        viewModel.smsResult.observe(this, Observer {
            it.onError { status, message ->
                Toast.makeText(applicationContext, "$status:${message}", Toast.LENGTH_LONG)
                    .show()
            }

        })


        viewModel.loginResult.observe(this) { result ->
            loading.visibility = View.GONE

            result.onSuccess {


                // Save
                viewModel.savePhone(phone.text.toString())
                if (savePassword.isChecked) {
                    viewModel.savePassword(password.text.toString())
                }


                updateUiWithUser(it)
            }.onError { status, message ->
                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
            }

            setResult(Activity.RESULT_OK)
        }

        viewModel.allowStart.observe(this) {
            if (it.allow.not()) {
                Toast.makeText(this.applicationContext, it.msg, Toast.LENGTH_LONG).show()
                exitProcess(-1)
            }
        }


        about.setOnClickListener {
            AboutDialog().show(this)
        }

        useCode.setOnClickListener {
            viewModel.useVerifyCode(useCode.isChecked)
            password.setText("")
        }

        phone.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    phone.text.toString()
                )
            }
            sms.setOnClickListener {
                viewModel.sendSMS(phone.text.toString())
            }
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    phone.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            phone.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                viewModel.login(phone.text.toString(), password.text.toString())
            }
        }
        savePassword.setOnClickListener {
            viewModel.isSavePasswordEnable = savePassword.isChecked
        }


        viewModel.updateLocalData()


    }

    private fun updateUiWithUser(model: User) {
        val welcome = getString(R.string.welcome)
        val token = model.token
        Toast.makeText(
            applicationContext,
            "$welcome $token",
            Toast.LENGTH_SHORT
        ).show()
        Intent(this, QueryActivity::class.java).apply {
            putExtra("token", model.token)
        }.let {
            this.startActivity(it)
        }
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
