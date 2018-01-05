package jsorgensen.com.smack.Controller

import BROADCAST_USER_DATA_CHANGE
import SOCKET_URL
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import io.socket.client.IO
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import jsorgensen.com.smack.Services.UserDataService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        socket.connect()
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
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
            }
        }
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
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userLoginButtonNavHeader.text = "Login"
        }

    }

    fun onAddChannelButtonNavHeaderClick(view: View){
        if(!AuthService.isLoggedIn){
            Toast.makeText(this, "You must login to add a channel.", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

        builder.setView(dialogView)
                .setPositiveButton("ADD"){ dialogInterface, i ->
                    val nameEditText = dialogView.findViewById<EditText>(R.id.addChannelNameEditText)
                    val descriptionEditText = dialogView.findViewById<EditText>(R.id.addChannelDescriptionEditText)
                    val channelName = nameEditText.text.toString()
                    val channelDescription = descriptionEditText.text.toString()

                    socket.emit("newChannel", channelName, channelDescription)
                }.setNegativeButton("CANCEL") { dialogInterface, i ->

                }.show()
    }

    fun onSendMessageButtonClick(view: View){
        hideKeyboard()

        if(!AuthService.isLoggedIn){
            Toast.makeText(this, "Please login to send message.", Toast.LENGTH_SHORT).show()
            return
        }

        messageEditText.text.clear()
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
