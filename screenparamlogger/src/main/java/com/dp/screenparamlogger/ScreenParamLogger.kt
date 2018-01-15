package com.dp.screenparamlogger

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.support.v4.app.ActivityCompat
import com.an.deviceinfo.device.model.Device
import com.dp.screenparamlogger.entry.ScreenData
import com.dp.screenparamlogger.exception.PermissionDeniedException
import com.jraska.falcon.Falcon
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ScreenParamLogger {

    private var deviceInfo: Device? = null

    private val fileDateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.US)

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = ScreenParamLogger()
    }

    companion object {
        var appVersion: String = "Not inited into Application"

        val instance: ScreenParamLogger by lazy { Holder.INSTANCE }
    }

    fun logActivity(activity: Activity, file: File = provideScreenshotFile(activity, provideFileName(activity.localClassName))): Observable<ScreenData> {
        if (instance.deviceInfo == null) {
            instance.deviceInfo = Device(activity)
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return Observable.error(PermissionDeniedException())
        }
        return Observable.just(file)
                .subscribeOn(Schedulers.io())
                .flatMap { createdFile ->
                    Falcon.takeScreenshot(activity, createdFile)
                    Observable.just(createdFile)
                }
                .flatMap { Observable.just(ScreenData(Date(), it.name, it, provideDeviceInfoFile(activity, it.nameWithoutExtension))) }
    }

    private fun provideDeviceInfoFile(context: Context, name: String): File {
        val textFileDir = File(context.externalCacheDir, "app_screens_device_data")
        if (!textFileDir.exists()) {
            textFileDir.mkdir()
        }
        val infoFile = File(textFileDir, "$name.txt")
        if (infoFile.createNewFile()) {
            try {
                val fileOutputStream = FileOutputStream(infoFile, true)
                fileOutputStream.write(prepareDeviceInfoBytes(context.resources))
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: IOException) {
            }

        }
        return infoFile
    }

    private fun prepareDeviceInfoBytes(resources: Resources): ByteArray {
        return ("Manufactorer: ${deviceInfo?.manufacturer}\n" +
                "Model: ${deviceInfo?.model}" +
                "Hardware: ${deviceInfo?.hardware}\n" +
                "BuildBrand: ${deviceInfo?.buildBrand}\n" +
                "ScreenDensity: ${deviceInfo?.screenDensity} or ${resources.displayMetrics.density}\n" +
                "ScreenWidth: ${deviceInfo?.screenWidth}\n" +
                "ScreenHeight: ${deviceInfo?.screenHeight}\n" +
                "OsVersion: ${deviceInfo?.osVersion}\n" +
                "AppVersion: $appVersion")
                .toByteArray()
    }

    private fun provideScreenshotFile(context: Context, screenShotFileName: String): File {
        val screenShotDir = File(context.externalCacheDir, "app_screens")
        if (!screenShotDir.exists()) {
            screenShotDir.mkdir()
        }
        val screenShotFile = File(screenShotDir, screenShotFileName)
        screenShotFile.createNewFile()
        return screenShotFile
    }

    private fun provideFileName(screenName: String): String = "screenshot_${screenName}_${fileDateFormat.format(Date())}.jpg"
}