package cn.miuihone.xiaowine.utils

import android.content.Context
import android.os.SystemClock
import android.text.format.Formatter
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext


object Utils {
    object BootTime {
        fun get(): String {
            val time = SystemClock.elapsedRealtime() / 1000
            var temp: Int
            val sb = StringBuffer()
            if (time > 3600) {
                temp = (time / 3600).toInt()
                sb.append(if (time / 3600 < 10) "0$temp:" else "$temp:")
                temp = (time % 3600 / 60).toInt()
                changeSeconds(time, temp, sb)
            } else {
                temp = (time % 3600 / 60).toInt()
                changeSeconds(time, temp, sb)
            }
            return sb.toString()
        }

        private fun changeSeconds(seconds: Long, temp: Int, sb: StringBuffer) {
            var temps = temp
            sb.append(if (temps < 10) "0$temps:" else "$temps:")
            temps = (seconds % 3600 % 60).toInt()
            sb.append(if (temps < 10) "0$temps" else "" + temps)
        }

    }

    fun getRunningApp(){
        val am = (appContext.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager).runningAppProcesses.size
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

    fun Any.formatSize(): String = Formatter.formatFileSize(appContext, this as Long)


    fun Any?.isNull(callback: () -> Unit) {
        if (this == null) callback()
    }

    fun Any?.isNotNull(callback: () -> Unit) {
        if (this != null) callback()
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}