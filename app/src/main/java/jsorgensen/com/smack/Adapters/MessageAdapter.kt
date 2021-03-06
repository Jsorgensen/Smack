package jsorgensen.com.smack.Adapters

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import jsorgensen.com.smack.Model.Message
import jsorgensen.com.smack.R
import jsorgensen.com.smack.Services.UserDataService
import org.w3c.dom.Text
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MECH on 1/6/2018.
 */
class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindMessage(context, messages[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_cell_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserAvatarImageView)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserNameTextView)
        val timeStamp = itemView?.findViewById<TextView>(R.id.messageTimeStampTextView)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBodyTextView)

        fun bindMessage(context: Context, message: Message){
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = formatTimeStamp(message.timeStamp)
            messageBody?.text = message.message
        }

        fun formatTimeStamp(rawTimeStamp: String): String{
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try{
                convertedDate = isoFormatter.parse(rawTimeStamp)
            }catch(e: ParseException){
                Log.d("PARSE", "Cannot parse date $rawTimeStamp")
            }

            val outDateString = SimpleDateFormat("EEE, h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)
        }
    }
}