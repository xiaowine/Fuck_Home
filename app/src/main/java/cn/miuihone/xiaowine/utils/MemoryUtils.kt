package cn.miuihone.xiaowine.utils

import android.app.ActivityManager
import android.content.Context
import android.os.StatFs
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author xiaow
 */
class MemoryUtils {
    /**
     * @return 内存总大小
     */
    var totalMem: Long = 0

    /**
     * @return 已经使用大小
     */
    var availMem: Long = 0

    /**
     * @return 可以使用/剩余大小
     */
    var usedMem: Long = 0

    /**
     * @return 可用百分比
     */
    val percentValue = 0

    var threshold: Long = 0

    /**
     * @return 是否处于低内存状态
     */
    var lowMemory = false

    fun getMemoryInfo(context: Context): MemoryUtils {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        totalMem = memoryInfo.totalMem
        availMem = memoryInfo.availMem
        usedMem = totalMem - availMem
        threshold = memoryInfo.threshold
        lowMemory = memoryInfo.lowMemory
        return this
    }

    fun getStorageInfo(file: File?): MemoryUtils {
        if (file != null) {
            val sf = getStatFs(file)
            if (sf != null) {
                val blockSize = sf.blockSizeLong
                val blockCount = sf.blockCountLong
                val availCount = sf.availableBlocksLong
                totalMem = blockSize * blockCount
                availMem = blockSize * availCount
                usedMem = blockSize * (blockCount - availCount)
            }
        }
        return this
    }

    fun getPartitionInfo(totalMemKey: String?, availMemKey: String?): MemoryUtils {
        totalMem = getOthersMemory(totalMemKey)
        availMem = getOthersMemory(availMemKey)
        usedMem = totalMem - availMem
        return this
    }

    fun getPartitionInfo(totalMemKey: String?, memKey: String?, used: Boolean): MemoryUtils {
        if (used) {
            totalMem = getOthersMemory(totalMemKey)
            usedMem = getOthersMemory(memKey)
            availMem = totalMem - usedMem
        } else {
            getPartitionInfo(totalMemKey, memKey)
        }
        return this
    }

    fun getPartitionInfo(totalMemKey: String?, availMemKey: String?, usedMemKey: String?): MemoryUtils {
        totalMem = getOthersMemory(totalMemKey)
        availMem = getOthersMemory(availMemKey)
        usedMem = getOthersMemory(usedMemKey)
        return this
    }

    private fun getStatFs(file: File): StatFs? {
        try {
            return StatFs(file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getOthersMemory(keyName: String?): Long {
        try {
            val list = toString(FileInputStream("/proc/meminfo")).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in list) {
                if (s.contains(keyName!!)) {
                    return s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].replace("k", "").replace("B", "").replace("K", "").replace("b", "").trim { it <= ' ' }.toLong() * 1024
                }
            }
        } catch (e: Exception) {
            return 0
        }
        return 0
        //return Formatter.formatFileSize(getBaseContext(), initial_memory);
    } // Byte转换为KB或者MB，内存大小规格化	}

    companion object {
        @Throws(IOException::class) fun toString(`is`: InputStream): String {
            val stringBuilder = StringBuilder()
            val bf = BufferedReader(InputStreamReader(`is`))
            var line: String?
            var emptyOrNewLine = ""
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(emptyOrNewLine).append(line)
                emptyOrNewLine = "\n"
            }
            `is`.close()
            bf.close()
            return stringBuilder.toString()
        }
    }
}