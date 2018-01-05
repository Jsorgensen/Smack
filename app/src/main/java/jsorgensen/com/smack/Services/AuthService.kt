package jsorgensen.com.smack.Services

import BROADCAST_USER_DATA_CHANGE
import URL_CREATE_USER
import URL_GET_USER
import URL_LOGIN
import URL_REGISTER
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import jsorgensen.com.smack.Controller.App
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by MECH on 1/3/2018.
 */
object AuthService  {

//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object  : StringRequest(Method.POST, URL_REGISTER, Response.Listener{response ->
            complete(true)
        }, Response.ErrorListener {error ->
            Log.d("Error","Could not register user, $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->

            try{
                App.sharedPreferences.userEmail = response.getString("user")
                App.sharedPreferences.authToken = response.getString("token")
                App.sharedPreferences.isLoggedIn = true
                complete(true)
            }catch(e: JSONException){
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {error ->
            Log.d("Error", "login request failed, $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
            try{
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                complete(true)
            }catch(e: JSONException){
                Log.d("JSON", "EXC ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("Error", "Could not add user, $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){
        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.sharedPreferences.userEmail}", null, Response.Listener{response ->
            try{
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)
            }catch(e: JSONException){
                Log.d("JSON", "EXC: ${e.localizedMessage}")
            }
        }, Response.ErrorListener { error ->
            Log.d("Error", "Could not find user.")
            complete(false)
        }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(findUserRequest)
    }
}