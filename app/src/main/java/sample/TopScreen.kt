package sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TopScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        val buttonPost = findViewById<Button>(R.id.map)
        buttonPost.setOnClickListener() {
            // Intentクラスのオブジェクトを生成
            //val intent = Intent(this@TopScreenActivity, MapActivity::class.java)
            val intent = Intent(this@TopScreenActivity, MapActivity::class.java)
            // 生成したオブジェクトを引数に画面を起動
            startActivity(intent)
        }

        val buttonCust = findViewById<Button>(R.id.cust)
        buttonCust.setOnClickListener() {
            // Intentクラスのオブジェクトを生成
            //val intent = Intent(this@TopScreenActivity, MapActivity::class.java)
            val intent = Intent(this@TopScreenActivity, CustListActivity::class.java)
            // 生成したオブジェクトを引数に画面を起動
            startActivity(intent)
        }
        val buttonSkj = findViewById<Button>(R.id.skj)
        buttonSkj.setOnClickListener() {
            // Intentクラスのオブジェクトを生成
            //val intent = Intent(this@TopScreenActivity, MapActivity::class.java)
            val intent = Intent(this@TopScreenActivity, SkjActivity::class.java)
            // 生成したオブジェクトを引数に画面を起動
            startActivity(intent)
        }
    }
}