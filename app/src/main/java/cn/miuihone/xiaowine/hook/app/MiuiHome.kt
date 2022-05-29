package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.miuihone.xiaowine.hook.BaseHook
import cn.miuihone.xiaowine.utils.LogUtils
import cn.miuihone.xiaowine.utils.MemoryUtils
import cn.miuihone.xiaowine.utils.Utils.catchNoClass
import cn.miuihone.xiaowine.utils.Utils.formatSize
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter


@SuppressLint("StaticFieldLeak")
object MiuiHome : BaseHook() {
    private lateinit var mTxtMemoryViewGroup: ViewGroup
    private lateinit var mTxtMemoryInfo1: TextView
    private lateinit var MemoryView: TextView
    private lateinit var StorageView: TextView
    private lateinit var ZarmView: TextView
    @SuppressLint("SetTextI18n") override fun init() {
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                LogUtils.i(isInit)
                if (!isInit) {
                    mTxtMemoryViewGroup = it.thisObject.getObjectAs("mTxtMemoryContainer")
                    LogUtils.i(mTxtMemoryViewGroup.childCount)
                    for (i in 0 until mTxtMemoryViewGroup.childCount) {
                        mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                    }
                    mTxtMemoryInfo1 = it.thisObject.getObjectAs("mTxtMemoryInfo1")
                    initView()
                    isInit=true
                }
                refreshDate()
            }
        }
    }

    private fun initView() {
        val memoryLayout = LinearLayout(appContext).apply {
            gravity = Gravity.START
            orientation = LinearLayout.VERTICAL
        }
        MemoryView = TextView(appContext).apply {
            setTextColor(mTxtMemoryInfo1.textColors)
            textSize = 14f
        }
        StorageView= MemoryView
        ZarmView= MemoryView
        LogUtils.i(MemoryView.text)
        memoryLayout.addView(MemoryView)
        mTxtMemoryViewGroup.addView(memoryLayout)
    }

    @SuppressLint("SetTextI18n") private fun refreshDate() {
        val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
        val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
        val swapInfo: MemoryUtils = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
        MemoryView.text = "可用：${memoryInfo.availMem.formatSize()} | 总共：${memoryInfo.totalMem.formatSize()}\n" + "可用：${storageInfo.availMem.formatSize()} | 总共：${storageInfo.totalMem.formatSize()}\n" + "可用：${swapInfo.availMem.formatSize()} | 总共：${swapInfo.totalMem.formatSize()}\n"
        StorageView.text = "可用：${memoryInfo.availMem.formatSize()} | 总共：${memoryInfo.totalMem.formatSize()}\n" + "可用：${storageInfo.availMem.formatSize()} | 总共：${storageInfo.totalMem.formatSize()}\n" + "可用：${swapInfo.availMem.formatSize()} | 总共：${swapInfo.totalMem.formatSize()}\n"
        ZarmView.text = "可用：${memoryInfo.availMem.formatSize()} | 总共：${memoryInfo.totalMem.formatSize()}\n" + "可用：${storageInfo.availMem.formatSize()} | 总共：${storageInfo.totalMem.formatSize()}\n" + "可用：${swapInfo.availMem.formatSize()} | 总共：${swapInfo.totalMem.formatSize()}\n"
    }
}