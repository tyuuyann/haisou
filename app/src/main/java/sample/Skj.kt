package sample

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import jsonClass.SkjItem
import jsonClass.SkjList
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate


class SkjActivity : AppCompatActivity(), AdapterView.OnItemClickListener{

    companion object {
        const val KEY_PARENT = "FOLDER"
        const val KEY_CHILD = "CATEGORY"
    }
    var skjItem = SkjItem("","","","","","")
    var aa:ArrayList<SkjItem> = arrayListOf(skjItem)
    var skjList = SkjList(aa)
    var splitDate = arrayListOf<String>()

    private lateinit var mAdapter: SimpleExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skj)

        val nowDate: LocalDate = LocalDate.now();
        splitDate = nowDate.toString().split("-") as ArrayList<String>

        val textYear = findViewById<TextView>(R.id.year)
        textYear.text = splitDate[0] + "年"
        val textMonth = findViewById<TextView>(R.id.month)
        textMonth.text = splitDate[1] + "月"

        init(splitDate[0], splitDate[1])

        // 戻るボタン押下
        val btnPreview = findViewById<Button>(R.id.before)
        btnPreview.setOnClickListener {
            // 1月だった場合
            if(splitDate[1] == "01"){
                // 前年の12月を設定
                splitDate[0] = (splitDate[0].toInt() - 1).toString()
                splitDate[1] = "12"
            }else{
                // -1月する
                val mainusMonth = (splitDate[1].toInt() - 1).toString().padStart(2,'0')
                splitDate[1] = mainusMonth
            }
            textYear.text = splitDate[0] + "年"
            textMonth.text = splitDate[1] + "月"
            // 設定されている日付のデータを取得
            init(splitDate[0], splitDate[1])
        }

        // 戻るボタン押下
        val btnNext = findViewById<Button>(R.id.after)
        btnNext.setOnClickListener {
            // 12月だった場合
            if(splitDate[1] == "12"){
                // 来年の1月を設定
                splitDate[0] = (splitDate[0].toInt() + 1).toString()
                splitDate[1] = "01"
            }else{
                // +1月する
                val mainusMonth = (splitDate[1].toInt() + 1).toString().padStart(2,'0')
                splitDate[1] = mainusMonth
            }
            textYear.text = splitDate[0] + "年"
            textMonth.text = splitDate[1] + "月"
            // 設定されている日付のデータを取得
            init(splitDate[0], splitDate[1])
        }

    }

    private fun loadFolders(dayNum: Int, skjList: SkjList) {
        val folders = mutableListOf<Map<String, String>>()
        val categoriesOfFolder = mutableListOf<MutableList<Map<String, String>>>()


        for (i in 1..dayNum) {
            var chkDayNum: String = "";
            // chkDayNum = if(i.toString().length == 1) "0$i" else i.toString()
            chkDayNum = i.toString().padStart(2,'0')

            var count: Int = 0;
            val skjInfo = arrayListOf<String>()
            // A .. BはBを含める下記は含めない場合
            for(k in 0 until skjList.size()){
                if(skjList[k].day == chkDayNum){
                    count += 1
                    skjInfo.add(skjList[k].from_time + "~        " + skjList[k].skj_info)
                }
            }
            val category = mapOf(KEY_PARENT to i.toString()+"(日)        " + count.toString() + "件")
            folders.add(category)

            val categories = mutableListOf<Map<String, String>>()

            if(count > 0){
                for (j in 0 until count) {
                    val todo = mapOf(KEY_CHILD to skjInfo[j])
                    categories.add(todo)
                }
            }
            categoriesOfFolder.add(categories)
        }

        mAdapter = SimpleExpandableListAdapter(
            this,
            folders,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf(KEY_PARENT),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            categoriesOfFolder,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf(KEY_CHILD),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        val listView = (findViewById(R.id.expandableList) as ExpandableListView)
        listView.setAdapter(mAdapter)
    }

    private fun init(year: String , month:String ){
        val json = JSONObject()

        json.put("year", year)
        json.put("month", month)
        HttpTask {
            if (it == null) {
                println("connection error")
                return@HttpTask
            }
            println(it)
            var json = Gson().fromJson(it, SkjList::class.java)
            skjList = if(json.size() > 0) json.copy() else SkjList(aa)
            loadFolders(createDay(year, month), skjList)
        }.execute("POST", "http://192.168.1.14:8080/crm/resource/haisou/skjList", json.toString())
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
//        val intent = Intent(
//            this.applicationContext, CustAddrActivity::class.java
//        )
//
//        // clickされたpositionのtextとphotoのID
//        val selectedCustName: String = scenes.get(position)
//        val selectCustKana: String = sample2.custInfoList.get(position).cust_kana
//        val selectAddr: String = sample2.custInfoList.get(position).addres
//        val selectTel: String = sample2.custInfoList.get(position).tel
//        val selectIdo: String = sample2.custInfoList.get(position).point_ido
//        val selectKeido: String = sample2.custInfoList.get(position).point_keido
//        val selectYubinNo: String = sample2.custInfoList.get(position).yubino
//        // インテントにセット
//        intent.putExtra("CustName", selectedCustName)
//        intent.putExtra("CustKana", selectCustKana)
//        intent.putExtra("Addr", selectAddr)
//        intent.putExtra("Tel", selectTel)
//        intent.putExtra("Ido", selectIdo)
//        intent.putExtra("Keido", selectKeido)
//        intent.putExtra("Yubin", selectYubinNo)
//
//        // SubActivityへ遷移
//        startActivity(intent)
    }

    private fun createDay(year:String, month: String): Int {
        var daynum = 0;

        // うるう年で2月の場合
        if((year.toInt() % 4) == 0 && month == "02"){
            daynum = 29
        }else{
            //その他の日数を設定
            when (month){
                "02" -> daynum = 28
                "01","03","05","07","08","10","12" -> daynum = 31
                "04","06","09","11" -> daynum = 30
            }
        }
        return daynum
    }
}