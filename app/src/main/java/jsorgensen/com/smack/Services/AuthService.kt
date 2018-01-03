package jsorgensen.com.smack.Services

import URL_REGISTER
import android.content.Context
import org.json.JSONObject

/**
 * Created by MECH on 1/3/2018.
 */
object AuthService  {

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){

        val URL = URL_REGISTER

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

    }
}