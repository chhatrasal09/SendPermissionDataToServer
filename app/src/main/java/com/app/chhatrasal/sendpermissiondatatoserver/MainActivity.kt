package com.app.chhatrasal.sendpermissiondatatoserver

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100
    private val BASE_URL = "http://18.188.159.19:3000/"
    private val permission =
            arrayListOf(Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS)
    private lateinit var infoTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoTextView = findViewById(R.id.info_text_view)
        getMapData(false)
        getPermissions()
    }

    private fun getPermissions() {
        if (ContextCompat.checkSelfPermission(this, permission.get(0))
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, permission.get(1))
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, permission.get(2))
                != PackageManager.PERMISSION_GRANTED) {
            getRequest()
        }
    }

    private fun getRequest() {
        ActivityCompat.requestPermissions(this, permission.toTypedArray(), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val requestMap: HashMap<String, Boolean> = getMapData(true)


        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @SuppressLint("SetTextI18n")
    private fun getMapData(apiCall: Boolean): HashMap<String, Boolean> {
        val map: HashMap<String, Boolean> = HashMap()
        map.put("callLogPermission", false)
        map.put("contactPermission", false)
        map.put("messagePermission", false)
        if (ContextCompat.checkSelfPermission(this, permission.get(0)) == PackageManager.PERMISSION_GRANTED) {
            map["callLogPermission"] = true
        }
        if (ContextCompat.checkSelfPermission(this, permission.get(1)) == PackageManager.PERMISSION_GRANTED) {
            map["contactPermission"] = true
        }
        if (ContextCompat.checkSelfPermission(this, permission.get(2)) == PackageManager.PERMISSION_GRANTED) {
            map["messagePermission"] = true
        }

        infoTextView.text = " callLogPermission = ${map["callLogPermission"]},\n" +
                "contactPermission = ${map["contactPermission"]},\n" +
                "messagePermission = ${map["messagePermission"]}"

        System.out.println(map.toString())
        if (apiCall) {
            val permissionMap: HashMap<String, Any> = HashMap()
            permissionMap.put("permissionStatus", map)
            getRetrofit().create(WebApiInterface::class.java)
                    .sendData(permissionMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<RequestData> {
                        override fun onComplete() {
                            Log.v("####", "Completed.")
                        }

                        override fun onSubscribe(d: Disposable) {
                            Log.v("####", "Subscribed.")
                        }

                        override fun onNext(t: RequestData) {
                            runOnUiThread { Toast.makeText(this@MainActivity, "Data Sent to Server", Toast.LENGTH_LONG).show() }
                            Log.v("####", "${t.status}")
                        }

                        override fun onError(e: Throwable) {
                            Log.v("####", "${e.printStackTrace()}")
                        }
                    })
        }

        return map;
    }
}
