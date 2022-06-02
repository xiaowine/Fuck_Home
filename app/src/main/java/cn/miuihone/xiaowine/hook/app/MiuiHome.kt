package cn.miuihone.xiaowine.hook.app


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
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
import cn.miuihone.xiaowine.utils.Utils
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
    private var TextViewList: List<String> = ArrayList<String>(listOf(
        "MemoryView",
        "ZarmView",
        "StorageView",
        "BootTime",
        "RunningAppTotal",
        "RunningServiceTotal",
    ))
    private lateinit var mTxtMemoryViewGroup: ViewGroup
    private lateinit var mTxtMemoryInfo1: TextView
    private const val threshold = 21

    @SuppressLint("SetTextI18n") override fun init() {
//        refresh view data
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "refreshMemoryInfo" }.hookAfter {
                LogUtils.i("refreshMemoryInfo")
                val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
                val swapInfo = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
                val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())


//
//                status color
                TextViewMaps.forEach { (name, view) ->
                    run {
                        when (name) {
                            "MemoryView" -> {
                                view.text = "运存可用：\t${memoryInfo.availMem.formatSize()} \t总共：\t${memoryInfo.totalMem.formatSize()}\t剩余：${memoryInfo.percentValue}%"
                                if (memoryInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(mTxtMemoryInfo1.textColors)
                                }
                            }
                            "ZarmView" -> {
                                view.text = "虚拟可用：\t${swapInfo.availMem.formatSize()} \t总共：${swapInfo.totalMem.formatSize()}\t剩余：${swapInfo.percentValue}%"
                                if (swapInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(mTxtMemoryInfo1.textColors)
                                }
                            }
                            "StorageView" -> {
                                view.text = "存储可用：\t${storageInfo.availMem.formatSize()} \t总共：\t${storageInfo.totalMem.formatSize()}\t剩余：${storageInfo.percentValue}%"
                                if (storageInfo.percentValue < threshold) {
                                    view.setTextColor(Color.RED)
                                } else {
                                    view.setTextColor(mTxtMemoryInfo1.textColors)
                                }
                            }
                            "BootTime" -> {
                                view.text = "已开机时间：\t${Utils.BootTime.get()}"
                            }
                            "RunningAppTotal" -> {
                                view.text = "运行中应用总数：\t${(appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses.size}"
                            }
                            "RunningServiceTotal" -> {
                                view.text = "运行中服务总数：\t${(appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(999).size}"
                            }
                        }
                    }
                }
            }
        }
//        hide the original view
        catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "onFinishInflate" }.hookAfter {
                LogUtils.i("onFinishInflate")
                mTxtMemoryViewGroup = it.thisObject.getObjectAs("mTxtMemoryContainer")
                it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
                for (i in 0 until mTxtMemoryViewGroup.childCount) {
                    mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                }
                mTxtMemoryInfo1 = it.thisObject.getObjectAs("mTxtMemoryInfo1")

                TextViewMaps.apply {
                    TextViewList.forEach { name ->
                        run {
                            this[name] = TextView(appContext).apply {
                                LogUtils.i(name)
                                setTextColor(mTxtMemoryInfo1.textColors)
                                textSize = 12f
                            }
                        }
                    }
                }
                val memoryLayout = LinearLayout(appContext).apply {
                    gravity = Gravity.CENTER
                    orientation = LinearLayout.VERTICAL
                }
                TextViewMaps.forEach { (_, view) ->
                    run { memoryLayout.addView(view) }
                }
                mTxtMemoryViewGroup.addView(memoryLayout)
            }
        }
    }
//    #dc143c


}