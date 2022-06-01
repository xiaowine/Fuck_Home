package cn.miuihone.xiaowine.utils

import android.os.SystemClock
import android.text.format.Formatter
import com.github.kyuubiran.ezxhelper.init.InitFields


object Utils {
    fun Long.formatTimeS(): String {
        var temp: Int
        val sb = StringBuffer()
        if (this > 3600) {
            temp = (this / 3600).toInt()
            sb.append(if (this / 3600 < 10) "0$temp:" else "$temp:")
            temp = (this % 3600 / 60).toInt()
            changeSeconds(this, temp, sb)
        } else {
            temp = (this % 3600 / 60).toInt()
            changeSeconds(this, temp, sb)
        }
        return sb.toString()
    }

    private fun changeSeconds(seconds: Long, temp: Int, sb: StringBuffer) {
        var temps = temp
        sb.append(if (temps < 10) "0$temps:" else "$temps:")
        temps = (seconds % 3600 % 60).toInt()
        sb.append(if (temps < 10) "0$temps" else "" + temps)
    }

    fun catchNoClass(callback: () -> Unit) {
        try {
            callback()
        } catch (e: NoSuchMethodException) {
            LogUtils.i("${e.message} 未找到class：${callback.javaClass.name}")
        } catch (e: NullPointerException) {
            LogUtils.i("${e.message} 未找到class：${callback.javaClass.name}")
        }
    }

    fun Any.formatSize(): String = Formatter.formatFileSize(InitFields.appContext, this as Long)

    // 返回开机时间，单位微妙
    fun bootTime(): Long = System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000000


    fun Any?.isNull(callback: () -> Unit) {
        if (this == null) callback()
    }

    fun Any?.isNotNull(callback: () -> Unit) {
        if (this != null) callback()
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}