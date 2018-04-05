package com.dp.screenparamlogger.util

import android.content.Context
import com.an.deviceinfo.device.model.Device
import com.dp.screenparamlogger.ScreenParamLogger

class DeviceInfoProvider(private val context: Context) {

    private var deviceInfo = Device(context)

    fun prepareDeviceInfoBytes(tag: String, time: String): ByteArray {
        return ("Tag Screen: $tag\n" +
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
                .toByteArray()
    }
}