package com.dp.screenparamlogger.exception

data class HasScreenException(private val screenName: String): Exception("$screenName has screenShot data")