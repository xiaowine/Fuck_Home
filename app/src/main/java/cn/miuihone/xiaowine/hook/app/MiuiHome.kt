package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.miuihone.xiaowine.hook.BaseHook
import cn.miuihone.xiaowine.utils.LogUtils
import cn.miuihone.xiaowine.utils.MemoryUtils
import cn.miuihone.xiaowine.utils.Utils.catchNoClass
import cn.miuihone.xiaowine.utils.Utils.formatSize
import cn.miuihone.xiaowine.utils.Utils.isNull
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.putObject


@SuppressLint("StaticFieldLeak")
object MiuiHome : BaseHook() {
    private var memoryView: TextView? = null
    @SuppressLint("SetTextI18n") override fun init() {
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                val mTxtMemoryInfo1 = it.thisObject.getObjectAs<TextView>("mTxtMemoryInfo1")
                val memoryInfoLayout = (mTxtMemoryInfo1.parent as LinearLayout)
                for (i in 0 until memoryInfoLayout.childCount) {
                    LogUtils.i(i.isNull())
                    LogUtils.i(memoryInfoLayout.getChildAt(i))
                    memoryInfoLayout.getChildAt(i).visibility = View.GONE
                }
                it.thisObject.putObject("mTxtMemoryInfo1", TextView(appContext))


                val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
                val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
                val swapInfo: MemoryUtils = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")

                val memoryLayout = LinearLayout(appContext).apply {
                    gravity = Gravity.START
                    orientation = LinearLayout.VERTICAL
                }
                memoryView.isNull {
                    memoryView = TextView(appContext).apply {
                        setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
                val a = memoryInfo.availMem.formatSize()
                LogUtils.i(a)
                memoryView!!.text = "可用：${a} | 总共：${memoryInfo.totalMem.formatSize()}"
                memoryLayout.addView(memoryView)
                (mTxtMemoryInfo1.parent as LinearLayout).addView(memoryLayout)


            }
        }
    }
}