package com.aandssoftware.aandsinventory.notification

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.aandssoftware.aandsinventory.BuildConfig
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CallBackListener
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class NotificationUtil {
    enum class NotificationType {
        ORDER_DELIVERED_INDICATE_TO_COMPANY,
        ORDER_PAYMENT_INDICATE_TO_COMPANY,
        ORDER_CONFIRM_INDICATE_TO_ADMIN,
        ENQUIRY_FOR_PRODUCT_PRICE
    }

    companion object {
        fun sendNotification(token: String, map: HashMap<String, String>, callBackListener: CallBackListener) {
            sendMessage(token,map,callBackListener)
        }

        fun onNewToken(token: String, context: Context) {
            val customerModel : CustomerModel? = SharedPrefsUtils.getUserPreference(context,SharedPrefsUtils.CURRENT_USER)
            customerModel?.id?.let {
                FirebaseUtil.getInstance().getCustomerDao().storeNotificationTokenToCustomerItem(customerModel, token, CallBackListener {})
            }
        }

        private fun sendMessage(token: String?, map: HashMap<String, String>, callBackListener: CallBackListener) {
            val jsonArray = JSONArray()
            jsonArray.put(token)
            object : AsyncTask<String?, String?, String?>() {
                override fun doInBackground(vararg p0: String?): String? {
                    try {
                        val root = JSONObject()
                        val notification = JSONObject()
                        notification.put("body", map[NotificationUtil.BODY])
                        notification.put("title", map[NotificationUtil.TITLE])
                        val data = JSONObject(map as Map<*, *>)
                        //data.put("message", map[NotificationUtil.MESSAGE])
                        root.put("notification", notification)
                        root.put("data", data)
                        root.put("registration_ids", jsonArray)
                        val result = postToFCM(root.toString())
                        return result
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    return null
                }
                override fun onPostExecute(result: String?) {
                    try {
                        val resultJson = JSONObject(result)
                        val success: Int = resultJson.getInt("success")//failure
                        callBackListener.getCallBack(if(success==1){true}else{false})
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callBackListener.getCallBack(false)
                    }
                }
            }.execute()
        }

        @Throws(IOException::class)
       private fun postToFCM(bodyString: String?): String {
            val mClient = OkHttpClient()
            val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
            val JSON = MediaType.parse("application/json; charset=utf-8")
            val body = RequestBody.create(JSON, bodyString)
            val request = Request.Builder()
                    .url(FCM_MESSAGE_URL)
                    .post(body)
                    .addHeader("Authorization", "key=" +BuildConfig.FIREBASE_SERVER_KEY)
                    .build()
            val response = mClient.newCall(request).execute()
            return response.body()!!.string()
        }


        val BODY = "body"
        val ORDER_ID = "order_id"
        val CUSTOMER_ID = "customer_id"
        val INVENTORY_ID = "inventory_id"
        val FLOW_ID = "flow_id"
        val NOTIFICATION_FLOW = "notification_flow"
        val TITLE = "title"
        val MESSAGE = "message"
        val NOTIFICATION_TABLE = "notification"
        val ORDER_DELIVERED_TITLE = "Order Delivered "
        val ORDER_PAYMENT_TITLE = "Payment is pending for invoice number "
        val NOTIFICATION_TYPE = "notification_type"
        val ORDER_CONFIRM_TITLE = "Order Confirmed by "
        val ENQUIRY_NOTIFICATION_TITLE = "Enquiry For Product "
    }
}
