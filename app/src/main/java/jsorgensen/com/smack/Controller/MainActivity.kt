package jsorgensen.com.smack.Controller

import BROADCAST_USER_DATA_CHANGE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import jsorgensen.com.smack.Services.UserDataService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                userLoginButtonNavHeader.text = "Logout"

                Log.d("USER_DATA_SERVICE", "name: ${UserDataService.name} \nEmail: ${UserDataService.email} \nImage: ${UserDataService.avatarName}")
                Log.d("UI_ELEMENTS", "name: ${userNameNavHeader.text.toString()} \nEmail: ${userEmailNavHeader.text.toString()}")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("RESUME_USER_DATA_SERVIC", "name: ${UserDataService.name} \nEmail: ${UserDataService.email} \nImage: ${UserDataService.avatarName}")
        Log.d("RESUME_UI_ELEMENTS", "name: ${userNameNavHeader.text.toString()} \nEmail: ${userEmailNavHeader.text.toString()}")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onLoginButtonNavHeaderClick(view: View){

        if(!AuthService.isLoggedIn){
            val loginIntent = Intent(this, LogInActivity::class.java)
            startActivity(loginIntent)
        }else{
            UserDataService.logout()
            userNameNavHeader.text = "Login"
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userLoginButtonNavHeader.text = "Login"
        }

    }

    fun onAddChannelButtonNavHeaderClick(view: View){

    }

    fun onSendMessageButtonClick(view: View){

    }
}
