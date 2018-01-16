package com.dp.screenparamlogger

import android.annotation.SuppressLint
import android.app.Activity
import com.dp.screenparamlogger.entry.PackedData
import com.dp.screenparamlogger.entry.ScreenData
import com.dp.screenparamlogger.exception.HasScreenException
import com.dp.screenparamlogger.exception.PermissionDeniedException
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

        val instance: ScreenParamLogger by lazy { Holder.INSTANCE }
    }

    fun logScreen(activity: Activity, userId: String = ScreenParamLogger.userId,
                  tag: String = "${activity.localClassName}_${userId}_",
                  file: File = provideScreenshotFile(activity, provideFileName(tag)),
                  checkOnceLogging: Boolean = true): Observable<PackedData> {
        initScreenStorage(activity)
        initDeviceInfo(activity)
        if (screensStorage != null && screensStorage!!.hasThisScreen(tag) && checkOnceLogging) {
            if (file.exists()) file.delete()
            return Observable.error(HasScreenException(tag))
        }
        if (PermissionProvider.checkStoragePermission(activity)) {
            return Observable.error(PermissionDeniedException())
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

    private fun initDeviceInfo(activity: Activity) {
        if (deviceInfoProvider == null) deviceInfoProvider = DeviceInfoProvider(activity)
    }

    private fun initScreenStorage(activity: Activity) {
        if (screensStorage == null) screensStorage = DataStorage(activity)
    }
}