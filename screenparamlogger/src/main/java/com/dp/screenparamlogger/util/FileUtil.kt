package com.dp.screenparamlogger.util

import android.content.Context
import com.dp.screenparamlogger.entry.PackedData
import com.dp.screenparamlogger.entry.ScreenData
import org.zeroturnaround.zip.FileSource
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    private val fileDateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.US)

    fun provideDeviceInfoFile(context: Context, name: String, deviceInfoBytes: ByteArray): File {
        val textFileDir = File(context.externalCacheDir, "app_screens_device_data")
        if (!textFileDir.exists()) {
            textFileDir.mkdir()
        }
        val infoFile = File(textFileDir, "$name.txt")
        if (infoFile.createNewFile()) {
            try {
                val fileOutputStream = FileOutputStream(infoFile, true)
                fileOutputStream.write(deviceInfoBytes)
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: IOException) {
            }

        }
        return infoFile
    }

    fun provideScreenshotFile(context: Context, screenShotFileName: String): File {
        val screenShotDir = File(context.externalCacheDir, "app_screens")
        if (!screenShotDir.exists()) {
            screenShotDir.mkdir()
        }
        val screenShotFile = File(screenShotDir, screenShotFileName)
        screenShotFile.createNewFile()
        return screenShotFile
    }

    fun provideFileName(screenName: String): String = "screenshot_${screenName}_${fileDateFormat.format(Date())}.jpg"

    fun packToZip(context: Context, screenData: ScreenData): PackedData {
        val archiveFileDir = File(context.externalCacheDir, "app_screens_device_data_into_zip")
        if (!archiveFileDir.exists()) {
            archiveFileDir.mkdir()
        }
        val zipFile = File(archiveFileDir, "${screenData.screenShotFile.nameWithoutExtension}.zip")
        zipFile.createNewFile()
        ZipUtil.pack(arrayOf(
                FileSource(screenData.screenShotFile.name, screenData.screenShotFile),
                FileSource(screenData.deviceInfoTextFile.name, screenData.deviceInfoTextFile)
        ), zipFile)
        return PackedData(screenData, zipFile)
    }
}