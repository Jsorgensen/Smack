package jsorgensen.com.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import jsorgensen.com.smack.R

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
    }

    fun onLoginLoginButtonClick(view: View){

    }

    fun onLoginCreateUserButtonClick(view: View){
        val createUserIntent= Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }
}
