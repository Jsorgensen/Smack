package jsorgensen.com.smack.Controller

import BROADCAST_USER_DATA_CHANGE
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import jsorgensen.com.smack.Services.UserDataService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_log_in.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE
    }

    fun onAvatarGeneratorClick(view: View){
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(color == 0)
            userAvatar = "light$avatar"
        else
            userAvatar = "dark$avatar"

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    fun onAvatarBackgroundClick(view: View){
        val random = Random()
        val red = random.nextInt(255)
        val green = random.nextInt(255)
        val blue = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(red, green, blue))

        val savedR = red.toDouble() / 255
        val savedG = green.toDouble() / 255
        val savedB = blue.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun onCreateUserClick(view: View){
        enableSpinner(true)
        val userName = createUserNameEditText.text.toString()
        val email = createUserEmailEditText.text.toString()
        val password = createUserPasswordEditText.text.toString()

        if(userName.isEmpty() || email.isEmpty() || password.isEmpty()){
            errorToast("Name, Email, or Password is empty.  These are all required fields.")
            enableSpinner(false)
            return
        }

        AuthService.registerUser(this, email, password){registerSuccess ->
            Toast.makeText(this@CreateUserActivity, "Request was ${if(registerSuccess)"successful" else "unsuccessful"}.", Toast.LENGTH_SHORT).show()
            if(registerSuccess){
                AuthService.loginUser(this, email, password){loginSuccess ->
                    if(loginSuccess){
                        AuthService.createUser(this, userName, email, userAvatar, avatarColor){createSuccess ->
                            if(createSuccess){
                                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                enableSpinner(false)
                                finish()
                            }else
                                errorToast("Failed to Create User")
                        }
                    }else
                        errorToast("Failed to Login")
                }
            }else
                errorToast("Failed to Register")
        }
    }

    fun errorToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            createSpinner.visibility = View.VISIBLE
        }else {
            createSpinner.visibility = View.INVISIBLE
        }
        createCreateUserButton.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        createAvatarBackgroundButton.isEnabled = !enable
    }
}
