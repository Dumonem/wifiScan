package kr.mediacore.wifi_scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val permission = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var wifiManager:WifiManager

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if(wifiManager != null) {
                if(!wifiManager.isWifiEnabled) {
                    Toast.makeText(this,"와이파이를 켜주세요",Toast.LENGTH_SHORT).show()
                } else {
                    val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                    registerReceiver(mWifiscanReceiver,filter)
                    val result = wifiManager.startScan();
                    val str = if(result) {
                        "스캔시작"
                    } else {
                        "스캔실패"
                    }
                    Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
                    wifiManager.scanResults
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(!checkPermission()) {
            finish()
        }
    }

    private val mWifiscanReceiver: BroadcastReceiver = object :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if(action != null) {
                if(action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
                    getWIFIScanResult()
                } else if(action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                    context?.sendBroadcast(Intent("wifi.ON_NETWORK_STATE_CHANGED"))
                }
            }
        }
    }

    //권한 필수 없으면 와이파이 검색 불가.
    private fun checkPermission(): Boolean {
        val listPermissionsNeeded = arrayListOf<String>()
        for(p in permission) {
            val result = ContextCompat.checkSelfPermission(this,p)
            if(result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if(listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(),100)
            return false
        }
        return true;
    }

    private fun getWIFIScanResult() {
        val scanResult = wifiManager.scanResults
        for(rst in scanResult) {
            println("scanResult : $rst")

        }
    }
}