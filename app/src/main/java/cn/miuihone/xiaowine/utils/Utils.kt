package cn.miuihone.xiaowine.utils

import android.text.format.Formatter
import com.github.kyuubiran.ezxhelper.init.InitFields


object Utils {
     fun catchNoClass(callback: () -> Unit) {
        try {
            callback()
        } catch (e: NoSuchMethodException) {
            LogUtils.i("${e.message} 未找到class：${callback.javaClass.name}")
        }
    }
    fun Any?.formatSize(): String =  Formatter.formatFileSize(InitFields.appContext, this as Long)
    fun Any?.isNull(callback: () -> Unit) {
        if (this == null) callback()
    }
    fun Any?.isNotNull(callback: () -> Unit) {
        if (this != null) callback()
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}