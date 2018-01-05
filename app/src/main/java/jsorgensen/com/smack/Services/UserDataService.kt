package jsorgensen.com.smack.Services

import android.graphics.Color
import jsorgensen.com.smack.R.id.userImageNavHeader

/**
 * Created by MECH on 1/4/2018.
 */
object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout(){
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }

    fun returnAvatarColor(components: String): Int{
                val rgbs = components.replace("([\\[\\]]+)".toRegex(), "").split(", ");

                val r = (rgbs[0].toDouble() * 255).toInt()
                val g = (rgbs[1].toDouble() * 255).toInt()
                val b = (rgbs[2].toDouble() * 255).toInt()

                return Color.rgb(r, g, b)
    }
}