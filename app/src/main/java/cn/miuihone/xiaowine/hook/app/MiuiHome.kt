package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.graphics.Color
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
import com.github.kyuubiran.ezxhelper.utils.putObject


@SuppressLint("StaticFieldLeak")
object MiuiHome : BaseHook() {
    private var TextViewMaps = LinkedHashMap<String, TextView>()
    private var mInit: Boolean = false
    private lateinit var mTxtMemoryViewGroup: ViewGroup
    private lateinit var mTxtMemoryInfo1: TextView
    private const val threshold = 20

    @SuppressLint("SetTextI18n") override fun init() {
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                LogUtils.i("refreshMemoryInfo")
//                refreshDate()
                val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
                val swapInfo = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
                val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
                TextViewMaps["MemoryView"]!!.text = "运存可用：\t${memoryInfo.availMem.formatSize()} \t总共：\t${memoryInfo.totalMem.formatSize()}\t剩余：${memoryInfo.percentValue}%"
                TextViewMaps["ZarmView"]!!.text = "虚拟可用：\t${swapInfo.availMem.formatSize()} \t总共：${swapInfo.totalMem.formatSize()}\t剩余：${swapInfo.percentValue}%"
                TextViewMaps["StorageView"]!!.text = "存储可用：\t${storageInfo.availMem.formatSize()} \t总共：\t${storageInfo.totalMem.formatSize()}\t剩余：${storageInfo.percentValue}%"

                TextViewMaps.forEach { (name, view) ->
                    run {
                        when (name) {
                            "MemoryView" -> {
                                if (memoryInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(Color.WHITE)
                                }
                            }
                            "ZarmView" -> {
                                if (swapInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(Color.WHITE)
                                }
                            }
                            "StorageView" -> {
                                if (storageInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(Color.WHITE)
                                }
                            }
                        }
                    }
                }
            }
        }
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "onFinishInflate" }.hookAfter {
                LogUtils.i("onFinishInflate")
                mTxtMemoryViewGroup = it.thisObject.getObjectAs("mTxtMemoryContainer")
//                hideView(it)
                it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
                for (i in 0 until mTxtMemoryViewGroup.childCount) {
                    mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                }

                mTxtMemoryInfo1 = it.thisObject.getObjectAs("mTxtMemoryInfo1")
//                initView()

                TextViewMaps.apply {
                    put("MemoryView", newTextView())
                    put("ZarmView", newTextView())
                    put("StorageView", newTextView())
                }
                val memoryLayout = LinearLayout(appContext).apply {
                    gravity = Gravity.CENTER
                    orientation = LinearLayout.VERTICAL
                }
                TextViewMaps.forEach { (_, view) ->
                    run { memoryLayout.addView(view) }
                }
                mTxtMemoryViewGroup.addView(memoryLayout)

                mInit = true
            }
        }
    }
//    #dc143c

//    @SuppressLint("SetTextI18n") private fun refreshDate() {
//        val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
//        val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
//        val swapInfo: MemoryUtils = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
//        TextViewMaps["MemoryView"]!!.text = "运存可用：\t${memoryInfo.availMem.formatSize()} \t总共：\t${memoryInfo.totalMem.formatSize()}\t剩余：${memoryInfo.percentValue}%"
//        TextViewMaps["ZarmView"]!!.text = "虚拟可用：\t${swapInfo.availMem.formatSize()} \t总共：${swapInfo.totalMem.formatSize()}\t剩余：${swapInfo.percentValue}%"
//        TextViewMaps["StorageView"]!!.text = "存储可用：\t${storageInfo.availMem.formatSize()} \t总共：\t${storageInfo.totalMem.formatSize()}\t剩余：${storageInfo.percentValue}%"
//    }

//    private fun initView() {
//        TextViewMaps.apply {
//            put("MemoryView", newTextView())
//            put("ZarmView", newTextView())
//            put("StorageView", newTextView())
//        }
//        val memoryLayout = LinearLayout(appContext).apply {
//            gravity = Gravity.CENTER
//            orientation = LinearLayout.VERTICAL
//        }
//        TextViewMaps.forEach { (_, view) ->
//            run { memoryLayout.addView(view) }
//        }
//        mTxtMemoryViewGroup.addView(memoryLayout)
//    }

    private fun newTextView(): TextView = TextView(appContext).apply {
        setTextColor(mTxtMemoryInfo1.textColors)
        textSize = 12f
    }

//    private fun hideView(it: XC_MethodHook.MethodHookParam) {
//        it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
//        for (i in 0 until mTxtMemoryViewGroup.childCount) {
//            mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
//        }
//    }

}