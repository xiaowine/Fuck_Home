package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.text.format.Formatter
import android.widget.TextView
import cn.miuihone.xiaowine.hook.BaseHook
import cn.miuihone.xiaowine.utils.LogUtils
import cn.miuihone.xiaowine.utils.Utils.catchNoClass
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.putObject


object MiuiHome : BaseHook() {

    @SuppressLint("SetTextI18n") override fun init() {
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                val mTxtMemoryInfo1 = it.thisObject.getObjectAs<TextView>("mTxtMemoryInfo1")
                LogUtils.i(mTxtMemoryInfo1.text.toString())
                val mTxtMemoryInfo = TextView(appContext)
                mTxtMemoryInfo1.text = "可用：${getMemory()} | 总共："
                it.thisObject.putObject("mTxtMemoryInfo1", mTxtMemoryInfo)
            }
        }
    }
    private fun getMemory():String{
        val am: ActivityManager = appContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val outInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(outInfo)
       return Formatter.formatFileSize(appContext, outInfo.availMem)
    }
}