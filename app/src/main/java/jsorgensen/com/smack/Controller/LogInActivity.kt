package jsorgensen.com.smack.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.AutoScrollHelper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        loginSpinner.visibility = View.INVISIBLE
    }

    fun onLoginLoginButtonClick(view: View){
        enableSpinner(true)
        val email = loginEmailEditText.text.toString()
        val password = loginPasswordEditText.text.toString()
        hideKeyboard()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Login requires both Email and Password.", Toast.LENGTH_LONG).show()
            return
        }

        AuthService.loginUser(this, email, password){loginSuccess ->
            if(loginSuccess){
                AuthService.findUserByEmail(this){findSuccess->
                    if(findSuccess){
                        finish()
                        enableSpinner(false)
                    }else
                        errorToast("Could not find User Emaila $email")
                }
            }else
                errorToast("Could not login for $email")
        }
    }

    fun onLoginCreateUserButtonClick(view: View){
        val createUserIntent= Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }


    fun errorToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            loginSpinner.visibility = View.VISIBLE
        }else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginLoginButton.isEnabled = !enable
        loginCreateUserButton.isEnabled = !enable
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
