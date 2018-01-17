package com.dp.screenparamlogger

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.dp.screenparamlogger.entry.PackedData
import com.dp.screenparamlogger.entry.ScreenData
import com.dp.screenparamlogger.exception.HasScreenException
import com.dp.screenparamlogger.exception.PermissionDeniedException
import com.dp.screenparamlogger.exception.WorkWithDebugDisabledException
import com.dp.screenparamlogger.storage.DataStorage
import com.dp.screenparamlogger.util.DeviceInfoProvider
import com.dp.screenparamlogger.util.FileUtil.packToZip
import com.dp.screenparamlogger.util.FileUtil.provideDeviceInfoFile
import com.dp.screenparamlogger.util.FileUtil.provideFileName
import com.dp.screenparamlogger.util.FileUtil.provideScreenshotFile
import com.dp.screenparamlogger.util.PermissionProvider
import com.jraska.falcon.Falcon
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

class ScreenParamLogger {

    private var deviceInfoProvider: DeviceInfoProvider? = null
    private var screensStorage: DataStorage? = null

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = ScreenParamLogger()
    }

    companion object {
        var appVersion: String = "No init into Application"
        var userId: String = ""
        var workWithDebug = true
        var outputLogEnable = true

        val instance: ScreenParamLogger by lazy { Holder.INSTANCE }
    }

    fun logScreen(activity: Activity, userId: String = ScreenParamLogger.userId,
                  tag: String = activity.localClassName,
                  file: File = provideScreenshotFile(activity, provideFileName("_${tag}_$userId")),
                  checkOnceLogging: Boolean = true): Observable<PackedData> {
        if (!workWithDebug && BuildConfig.DEBUG) {
            if (file.exists()) file.delete()
            printLog(WorkWithDebugDisabledException())
            return Observable.empty()
        }
        initScreenStorage(activity)
        initDeviceInfo(activity)
        if (screensStorage != null && screensStorage!!.hasThisScreen(tag) && checkOnceLogging) {
            if (file.exists()) file.delete()
            printLog(HasScreenException(tag))
            return Observable.empty()
        }
        if (PermissionProvider.checkStoragePermission(activity)) {
            if (file.exists()) file.delete()
            printLog(PermissionDeniedException())
            return Observable.empty()
        }
        return Observable.just(file)
                .subscribeOn(Schedulers.io())
                .flatMap { createdFile ->
                    Falcon.takeScreenshot(activity, createdFile)
                    Observable.just(createdFile)
                }
                .flatMap {
                    Observable.just(ScreenData(Date(), it.name, it, provideDeviceInfoFile(
                            activity,
                            it.nameWithoutExtension,
                            deviceInfoProvider!!.prepareDeviceInfoBytes())))
                }
                .flatMap { Observable.just(packToZip(activity, it)) }
                .doOnNext { if (screensStorage != null) screensStorage?.saveLoggedScreen(tag) }
    }

    private fun printLog(exception: Exception) {
        if(outputLogEnable) Log.i(javaClass.simpleName, exception.message)
    }

    private fun initDeviceInfo(activity: Activity) {
        if (deviceInfoProvider == null) deviceInfoProvider = DeviceInfoProvider(activity)
    }

    private fun initScreenStorage(activity: Activity) {
        if (screensStorage == null) screensStorage = DataStorage(activity)
    }
}