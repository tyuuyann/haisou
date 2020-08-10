package sample

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

operator fun JSONArray.iterator(): Iterator<JSONObject> =
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

val TIMEOUT = 10*1000

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //findViewById<TextView>(R.id.main_text).text = hello()

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

//        val button = findViewById<Button>(R.id.clear)
//        button.setOnClickListener() {
//            progressBar.visibility = View.VISIBLE
//            HttpTask {
//                progressBar.visibility = View.INVISIBLE
//                if (it == null) {
//                    println("connection error")
//                    return@HttpTask
//                }
//                for (json in JSONArray(it)) {
//                    println(json)
//                }
//            }.execute("GET",  "http://192.168.1.13:8080/crm/resource/sample2/login")
//        }

        val buttonPost = findViewById<Button>(R.id.login)
        buttonPost.setOnClickListener() {
            val user = findViewById<TextView>(R.id.inputID).text.toString()
            val pass =  findViewById<TextView>(R.id.inputPass).text.toString()
            var inputChk = false

            if("" == user && "" == pass.toString()){
                findViewById<TextView>(R.id.ErrorMessage).text = "ID、パスワードを入力してください。"
                inputChk = true
            } else if ("" == user.toString()){
                findViewById<TextView>(R.id.ErrorMessage).text = "IDを入力してください。"
                inputChk = true
            } else if ("" == pass.toString()){
                findViewById<TextView>(R.id.ErrorMessage).text = "パスワードを入力してください。"
                inputChk = true
            } else {
                findViewById<TextView>(R.id.ErrorMessage).text = ""
            }

            if(!inputChk){
                val json = JSONObject()

                json.put("loginUser", user)
                json.put("loginPswd", pass)

                progressBar.visibility = View.VISIBLE
                HttpTask {
                    progressBar.visibility = View.INVISIBLE
                    if (it == null) {
                        println("connection error")
                        return@HttpTask
                    }
                    println(it)
                    var chekOK = feickJson(it.toString())

                    if(chekOK[1] == "OK"){
                        // Intentクラスのオブジェクトを生成
                        val intent = Intent(this@MainActivity, TopScreenActivity::class.java)
                        // 生成したオブジェクトを引数に画面を起動
                        startActivity(intent)
                    } else {
                        findViewById<TextView>(R.id.ErrorMessage).text = "IDまたはパスワードが違います。"
                    }
                }.execute("POST", "http://192.168.1.14:8080/crm/resource/sample2/login2", json.toString())
                    //}.execute("POST", "http://match.mydns.jp:8080/crm/resource/sample2/login2", json.toString())

             }
        }
    }

    private fun feickJson(json : String) : ArrayList<String> {
        var  a = json.replace("{","")

        a = a.replace("}","")
        a = a.replace("\"","")

        var b = a.split(",")

        val returnJson = arrayListOf("")
        for(i in b){
            var c = i.split(":")
            returnJson.add(c[1])
        }

        return returnJson
    }

    class HttpTask(callback: (String?) -> Unit) : AsyncTask<String, Unit, String>()  {

        var callback = callback

        override fun doInBackground(vararg params: String): String? {
            val url = URL(params[1])
            val httpClient = url.openConnection() as HttpURLConnection
            httpClient.readTimeout = TIMEOUT
            httpClient.connectTimeout = TIMEOUT
            httpClient.requestMethod = params[0]

            if (params[0] == "POST") {
                httpClient.instanceFollowRedirects = false
                httpClient.doOutput = true
                httpClient.doInput = true
                httpClient.useCaches = false
                httpClient.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            try {
                if (params[0] == "POST") {
                    httpClient.connect()
                    val os = httpClient.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    writer.write(params[2])
                    writer.flush()
                    writer.close()
                    os.close()
                }
                if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = BufferedInputStream(httpClient.inputStream)
                    return readStream(inputStream = stream)
                } else {
                    println("ERROR ${httpClient.responseCode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }

            return null
        }

        private fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            callback(result)
        }
    }

    companion object {
        val TAG = "MainActivity"
    }
}