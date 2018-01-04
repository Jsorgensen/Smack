package jsorgensen.com.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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
        val email = createUserEmailEditText.text.toString()
        val password = createUserPasswordEditText.text.toString()

        AuthService.registerUser(this, email, password){registerSuccess ->
            Toast.makeText(this@CreateUserActivity, "Request was ${if(registerSuccess)"successful" else "unsuccessful"}.", Toast.LENGTH_SHORT).show()
            if(registerSuccess){
                AuthService.loginUser(this, email, password){loginSuccess ->
                    if(loginSuccess){
                        Toast.makeText(this@CreateUserActivity, "Login Success! \n ${AuthService.userEmail} \n${AuthService.authToken}", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }
}
