package jsorgensen.com.smack.Services

import URL_GET_CHANNELS
import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import jsorgensen.com.smack.Controller.App
import jsorgensen.com.smack.Model.Channel
import jsorgensen.com.smack.Model.Message
import org.json.JSONException

/**
 * Created by MECH on 1/5/2018.
 */
object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){
        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            try {
                for(x in 0 until response.length()){
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val description = channel.getString("description")
                    val id = channel.getString("_id")

                    val newChannel = Channel(name, description, id)
                    this.channels.add(newChannel)
                }
                complete(true)
            }catch(e: JSONException){
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("Error", "Could not get channels.")
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

        App.sharedPreferences.requestQueue.add(channelsRequest)
    }
}