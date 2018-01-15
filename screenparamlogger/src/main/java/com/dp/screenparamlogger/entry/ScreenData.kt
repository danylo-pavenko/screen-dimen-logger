package com.dp.screenparamlogger.entry

import java.io.File
import java.util.*

data class ScreenData(val date: Date, val name: String, val screenShotFile: File, val deviceInfoTextFile: File)