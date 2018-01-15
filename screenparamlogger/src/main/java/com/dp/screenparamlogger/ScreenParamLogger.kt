package com.dp.screenparamlogger

import android.app.Activity

class ScreenParamLogger {

    companion object {
        private var instance: ScreenParamLogger? = null
            get() {
                if (field == null) field = ScreenParamLogger()
                return field
            }
    }

    fun logActivity(activity: Activity) {

    }
}