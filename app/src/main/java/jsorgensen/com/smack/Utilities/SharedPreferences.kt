package jsorgensen.com.smack.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley
import jsorgensen.com.smack.Services.AuthService

/**
 * Created by MECH on 1/5/2018.
 */
class SharedPreferences(context: Context) {
    val PREFERENCES_FILENAME = "preferences"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, 0)

    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_EMAIL = "userEmail"

    var isLoggedIn : Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken : String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail : String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}