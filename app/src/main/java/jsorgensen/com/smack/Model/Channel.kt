package jsorgensen.com.smack.Model

/**
 * Created by MECH on 1/5/2018.
 */
class Channel(val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
    }
}