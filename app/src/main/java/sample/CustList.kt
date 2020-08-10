package sample

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import jsonClass.Sample1
import jsonClass.Sample2
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class CustListActivity : AppCompatActivity(), AdapterView.OnItemClickListener{

    var sample1 = Sample1("","","","","","","","")
    var aa:ArrayList<Sample1> = arrayListOf(sample1)
    var sample2 = Sample2(aa)
    val scenes = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custlist)

        HttpTask {
            if (it == null) {
                println("connection error")
                return@HttpTask
            }
            //println(it)
            //var json = Gson().fromJson(it, Test::class.java)Sample2
            var json = Gson().fromJson(it, Sample2::class.java)
            //var chekOK = feickJson(it.toString())
            for(x in json.custInfoList){
                scenes.add(x.cust_name)
            }

            sample2 = json.copy()

            // ListViewのインスタンスを生成
            val listView: ListView = findViewById(R.id.list_view)

            // BaseAdapter を継承したadapterのインスタンスを生成
            // レイアウトファイル list.xml を activity_main.xml に
            // inflate するためにadapterに引数として渡す

            // BaseAdapter を継承したadapterのインスタンスを生成
            // レイアウトファイル list.xml を activity_main.xml に
            // inflate するためにadapterに引数として渡す
            val adapter: BaseAdapter = ListViewAdapter(
                this.applicationContext,
                R.layout.list, scenes
            )

            // ListViewにadapterをセット
            listView.adapter = adapter

            // クリックリスナーをセット
            listView.onItemClickListener = this
        }.execute("POST", "http://192.168.1.14:8080/crm/resource/haisou/custinfoList", "")
        //}.execute("POST", "http://match.mydns.jp:8080/crm/resource/sample2/login2", "")

    }


    class HttpTask(callback: (String?) -> Unit) : AsyncTask<String, Unit, String>() {

        var callback = callback

        override fun doInBackground(vararg params: String): String? {
            val url = URL(params[1])
            val httpClient = url.openConnection() as HttpURLConnection
            //httpClient.readTimeout = TIMEOUT
            //httpClient.connectTimeout = TIMEOUT
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

    override fun onItemClick(
        parent: AdapterView<*>?, v: View?,
        position: Int, id: Long
    ) {
        val intent = Intent(
            this.applicationContext, CustAddrActivity::class.java
        )

        // clickされたpositionのtextとphotoのID
        val selectedCustName: String = scenes.get(position)
        val selectCustKana: String = sample2.custInfoList.get(position).cust_kana
        val selectAddr: String = sample2.custInfoList.get(position).addres
        val selectTel: String = sample2.custInfoList.get(position).tel
        val selectIdo: String = sample2.custInfoList.get(position).point_ido
        val selectKeido: String = sample2.custInfoList.get(position).point_keido
        val selectYubinNo: String = sample2.custInfoList.get(position).yubino
        // インテントにセット
        intent.putExtra("CustName", selectedCustName)
        intent.putExtra("CustKana", selectCustKana)
        intent.putExtra("Addr", selectAddr)
        intent.putExtra("Tel", selectTel)
        intent.putExtra("Ido", selectIdo)
        intent.putExtra("Keido", selectKeido)
        intent.putExtra("Yubin", selectYubinNo)

        // SubActivityへ遷移
        startActivity(intent)
    }
}