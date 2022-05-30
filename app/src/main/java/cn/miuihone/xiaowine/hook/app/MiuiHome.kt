package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.miuihone.xiaowine.hook.BaseHook
import cn.miuihone.xiaowine.utils.MemoryUtils
import cn.miuihone.xiaowine.utils.Utils.catchNoClass
import cn.miuihone.xiaowine.utils.Utils.formatSize
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObjectAs
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.putObject
import de.robv.android.xposed.XC_MethodHook
import java.util.HashMap


@SuppressLint("StaticFieldLeak")
object MiuiHome : BaseHook() {
    private var TextViewMaps = HashMap<String, TextView>().apply {
        put("MemoryView", newTextView())
        put("StorageView", newTextView())
        put("ZarmView", newTextView())
    }
    private var mInit: Boolean = false
    private lateinit var mTxtMemoryViewGroup: ViewGroup
    private lateinit var mTxtMemoryInfo1: TextView

    @SuppressLint("SetTextI18n") override fun init() {
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                if (!mInit) {
                    mTxtMemoryViewGroup = it.thisObject.getObjectAs("mTxtMemoryContainer")
                    hideView(it)
                    mTxtMemoryInfo1 = it.thisObject.getObjectAs("mTxtMemoryInfo1")
                    initView()
                    mInit = true
                }
                refreshDate()
            }
        }
    }

    @SuppressLint("SetTextI18n") private fun refreshDate() {
        val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
        val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
        val swapInfo: MemoryUtils = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
        TextViewMaps["MemoryView"]!!.text = "运存可用：\t${memoryInfo.availMem.formatSize()} \t| \t总共：\t${memoryInfo.totalMem.formatSize()}"
        TextViewMaps["ZarmView"]!!.text = "虚拟可用：\t${swapInfo.availMem.formatSize()} \t|\t总共：${swapInfo.totalMem.formatSize()}"
        TextViewMaps["StorageView"]!!.text = "存储可用：\t${storageInfo.availMem.formatSize()} \t|\t总共：\t${storageInfo.totalMem.formatSize()}"
    }

    private fun initView() {
        val memoryLayout = LinearLayout(appContext).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
        }
        TextViewMaps.forEach { (_, view) ->
            run { memoryLayout.addView(view) }
        }
        mTxtMemoryViewGroup.addView(memoryLayout)
    }

    private fun newTextView(): TextView = TextView(appContext).apply {
        setTextColor(mTxtMemoryInfo1.textColors)
        textSize = 12f
    }

    private fun hideView(it: XC_MethodHook.MethodHookParam) {
        it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
        for (i in 0 until mTxtMemoryViewGroup.childCount) {
            mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
        }
    }

}