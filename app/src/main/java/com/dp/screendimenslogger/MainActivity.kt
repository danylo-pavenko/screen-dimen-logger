package com.dp.screendimenslogger

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.dp.screenparamlogger.ScreenParamLogger
import com.intentfilter.androidpermissions.PermissionManager
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onWriteScreenLog(v: View) {
        ScreenParamLogger.appVersion = BuildConfig.VERSION_NAME
        PermissionManager.getInstance(this)
                .checkPermissions(Collections.singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), object : PermissionManager.PermissionRequestListener {
                    override fun onPermissionGranted() {
                        ScreenParamLogger.instance.logActivity(this@MainActivity)
                                .subscribe({ Log.i(javaClass.simpleName, "Logged SUCCESS") }, { Log.e(javaClass.simpleName, "Logged FAIL") })
                    }

                    override fun onPermissionDenied() {
                    }
                })
    }
}
