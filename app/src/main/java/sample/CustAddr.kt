package sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CustAddrActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var Ido: String = ""
    var Keido: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custaddres)

////        val mapFragment = supportFragmentManager
////            .findFragmentById(R.id.mapView) as SupportMapFragment
//        val mapFragment = MapFragment.newInstance()
//
//        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
////        fragmentTransaction.add(R.id.mapView, mapFragment)
//        //fragmentTransaction.add(R.id.google , mapFragment)
//        fragmentTransaction.commit()
//
//        mapFragment.getMapAsync(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // MainActivityからintentで受け取ったものを取り出す
        val selectedCustName = intent.getStringExtra("CustName")
        val selectedCustKana = intent.getStringExtra("CustKana")
        val selectAddr = intent.getStringExtra("Addr")
        val selectTel = intent.getStringExtra("Tel")
        Ido = intent.getStringExtra("Ido")
        Keido = intent.getStringExtra("Keido")
        val selectYubinNo = intent.getStringExtra("Yubin")

        val textView = findViewById<TextView>(R.id.cust_name)
        textView.text = selectedCustName
        val textView2 = findViewById<TextView>(R.id.cust_kana)
        textView2.text = selectedCustKana
        val textView3 = findViewById<TextView>(R.id.cust_addr)
        textView3.text = selectAddr
        val textView4 = findViewById<TextView>(R.id.cust_yubin)
        textView4.text = "〒　" + selectYubinNo
        val textView5 = findViewById<TextView>(R.id.cust_tel)
        textView5.text = "☎ "  + selectTel
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val osakaStation = Ido.toDoubleOrNull()?.let { Keido.toDoubleOrNull()?.let { it1 -> LatLng(it, it1) } }

        //val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(osakaStation?.let { MarkerOptions().position(it).title("Marker in Sydney") })
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(osakaStation, 20.0f))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}