package com.aandssoftware.aandsinventory.pdfgenarator

import android.content.Context
import android.os.AsyncTask
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class PdfHandler() {

    companion object{
      var  DEFAULT_SECRETE_KAY : String = "xHdVqfkmy68wtEdT"
      var  SECRETE_KAY : String = "secreteKey"
      var  DEFAULT_API_KAY : String = "495272875"
      var  API_KAY : String = "apiKey"
    }

    data class FileValue(val Name : String?,val Data :String?)
    data class InputFile(val Name : String?, val FileValue :FileValue?)
    data class UploadInput(val parameters : ArrayList<InputFile>?)

    data class OutputFileValue(val FileName : String?,val FileExt :String?,val FileSize : Int,val FileData : String?)
    data class UploadOutput(val Files : ArrayList<OutputFileValue>?,val ConversionCost : Int,val Code : Int?,val Message :String?)




   /* public fun uploadFile(context: Context,request : UploadInput, callBackListener: DownloadFileListener) {
        object : AsyncTask<String?, String?, String?>() {
            override fun doInBackground(vararg p0: String?): String? {
                try {
                    val result = requestToDownload(request,context)
                    return result
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                return null
            }
            override fun onPostExecute(result: String?) {
                try {
                    var uploadResult= Gson().fromJson(result,UploadOutput::class.java)
                    callBackListener.getDownloadedFile(uploadResult)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callBackListener.getDownloadedFile(null)
                }
            }
        }.execute()
    }*/

    @Throws(IOException::class)
    public fun requestToDownload(bodyString: UploadInput?,context: Context): UploadOutput {
        val mClient = OkHttpClient()
        val apiUrl = "https://v2.convertapi.com/convert/xlsx/to/pdf?Secret="+ SharedPrefsUtils.getStringPreference(context,SECRETE_KAY)
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(mediaType, Gson().toJson(bodyString))
        val request = Request.Builder()
                .url(apiUrl)
                .post(body)
                //.addHeader("Authorization", "key=" + BuildConfig.FIREBASE_SERVER_KEY)
                .build()
        val response = mClient.newCall(request).execute()
        return  Gson().fromJson(response.body()?.string(),UploadOutput::class.java)
    }


}

public interface DownloadFileListener {
    fun getDownloadedFile(result: PdfHandler.UploadOutput?)
}