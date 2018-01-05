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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import io.socket.client.IO
import io.socket.emitter.Emitter
import jsorgensen.com.smack.Model.Channel
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.AuthService
import jsorgensen.com.smack.Services.MessageService
import jsorgensen.com.smack.Services.UserDataService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setUpAdapters(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channelListView.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated", onNewChannel)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setUpAdapters()

        channelListView.setOnItemClickListener{_, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.sharedPreferences.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if(App.sharedPreferences.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                userLoginButtonNavHeader.text = "Logout"

                MessageService.getChannels{complete ->
                    if(complete){
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "${selectedChannel?.name}"
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onLoginButtonNavHeaderClick(view: View){

        if(!App.sharedPreferences.isLoggedIn){
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
        if(!App.sharedPreferences.isLoggedIn){
            Toast.makeText(this, "You must login to add a channel.", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

        builder.setView(dialogView)
                .setPositiveButton("ADD"){ _, _ ->
                    val nameEditText = dialogView.findViewById<EditText>(R.id.addChannelNameEditText)
                    val descriptionEditText = dialogView.findViewById<EditText>(R.id.addChannelDescriptionEditText)
                    val channelName = nameEditText.text.toString()
                    val channelDescription = descriptionEditText.text.toString()

                    socket.emit("newChannel", channelName, channelDescription)
                }.setNegativeButton("CANCEL") { _, _ ->

                }.show()
    }

    private val onNewChannel = Emitter.Listener {args ->
        runOnUiThread{
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    fun onSendMessageButtonClick(view: View){
        hideKeyboard()

        if(!App.sharedPreferences.isLoggedIn){
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
