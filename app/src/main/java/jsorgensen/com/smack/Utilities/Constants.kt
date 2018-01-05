/**
 * Created by MECH on 1/3/2018.
 */

const val BASE_URL = "https://jsorgensen-chat.herokuapp.com/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"

//Broadcast constants

const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"