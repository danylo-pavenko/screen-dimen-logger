package com.dp.screenparamlogger.util

import android.content.Context
import android.util.Log
import com.an.deviceinfo.device.model.Device
import com.dp.screenparamlogger.ScreenParamLogger

class DeviceInfoProvider(private val context: Context) {

    private var deviceInfo = Device(context)

    fun prepareDeviceInfoBytes(tag: String, time: String, mapData: HashMap<String, String>): ByteArray {
        var info = ("Tag Screen: $tag\n" +
                "Time: $time\n" +
                "Manufacturer: ${deviceInfo.manufacturer}\n" +
                "Model: ${deviceInfo.model}\n" +
                "Hardware: ${deviceInfo.hardware}\n" +
                "BuildBrand: ${deviceInfo.buildBrand}\n" +
                "ScreenDensity: ${deviceInfo.screenDensity} or ${context.resources.displayMetrics.density}\n" +
                "ScreenWidth: ${deviceInfo.screenWidth}\n" +
                "ScreenHeight: ${deviceInfo.screenHeight}\n" +
                "AndroidOsVersion: ${deviceInfo.osVersion}\n" +
                "Locale: ${deviceInfo.language}\n" +
                "AppVersion: ${ScreenParamLogger.appVersion}")
        try {
            for ((key, value) in mapData) {
                info += "\n$key: $value"
            }
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Your device make a crash")
        }
        return info.toByteArray()
    }
}