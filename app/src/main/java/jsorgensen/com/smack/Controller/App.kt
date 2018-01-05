package jsorgensen.com.smack.Controller

import android.app.Application
import jsorgensen.com.smack.Utilities.SharedPreferences

/**
 * Created by MECH on 1/5/2018.
 */
class App: Application() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        sharedPreferences = SharedPreferences(applicationContext)
        super.onCreate()
    }
}