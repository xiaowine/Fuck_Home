@file:Suppress("DEPRECATION")

package cn.fuckhome.xiaowine.hook.module

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.FileUtils
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.MemoryUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import cn.fuckhome.xiaowine.utils.Utils.formatSize
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.utils.*
import java.io.File
import kotlin.math.roundToInt

@SuppressLint("StaticFieldLeak")
object AddInfo : BaseHook() {


    lateinit var textColors: ColorStateList
    private var height: Int = 0
    private var TextViewMaps = LinkedHashMap<String, TextView>()
    private var TextViewList = arrayListOf<String>()
    private lateinit var mLinearLayout: LinearLayout

    private val metrics = appContext.resources.displayMetrics
    private val widthPixels = metrics.widthPixels
    private val heightPixels = metrics.heightPixels

    const val threshold = 50

    private val moduleReceiver by lazy { ModuleReceiver() }

    @SuppressLint("SetTextI18n")
    override fun init() {
        val resourceId: Int = appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        height = appContext.resources.getDimensionPixelSize(resourceId)
//        初始化根控件
        findConstructor("com.miui.home.recents.views.RecentsContainer") { parameterCount == 2 }.hookAfter {
            val mView = it.thisObject as FrameLayout
            mLinearLayout = LinearLayout(appContext).apply {
                orientation = LinearLayout.VERTICAL
                if (XConfig.getBoolean("optimizeAnimation")) {
                    visibility = View.GONE
                }
            }
            mView.addView(mLinearLayout)
            listOf("MemoryView", "ZarmView", "StorageView", "BootTime", "RunningAppTotal", "RunningServiceTotal").forEach { s ->
                LogUtils.i(XConfig.getBoolean(s))
                if (XConfig.getBoolean(s)) {
                    TextViewList.add(s)
                }
                LogUtils.i(TextViewList)
            }
        }
//        初始化添加控件
        Utils.catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "onFinishInflate" }.hookAfter {
                LogUtils.i("onFinishInflate")
                val mTxtMemoryViewGroup = it.thisObject.getObjectAs<ViewGroup>("mTxtMemoryContainer")
//                mTxtMemoryViewGroup.setBackgroundColor(Color.BLUE)
                it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
                for (i in 0 until mTxtMemoryViewGroup.childCount) {
                    mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                }
                val mTxtMemoryInfo1 = it.thisObject.getObjectAs<TextView>("mTxtMemoryInfo1")
                textColors = mTxtMemoryInfo1.textColors

                TextViewMaps.apply {
                    TextViewList.forEach { name ->
                        val view = TextView(appContext).apply {
                            LogUtils.i("Init view $name")
                            setBackgroundColor(Color.parseColor(XConfig.getBgColor()))
                            Utils.viewColor(this, null)
                            gravity = XConfig.getGravity()
                            textSize = 12f
                            marqueeRepeatLimit = -1
                            isSingleLine = true
                            maxLines = 1
                        }
                        mLinearLayout.addView(view)
                        this[name] = view
                    }
                }

            }
        }
        if (XConfig.getBoolean("optimizeAnimation")) {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "onApplyWindowInsets" }.hookAfter {
                LogUtils.i("refreshMemoryInfo")
                mLinearLayout.visibility = View.VISIBLE
            }
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "startRecentsContainerFadeOutAnim" }.hookAfter {
                LogUtils.i("startRecentsContainerFadeOutAnim")
                mLinearLayout.visibility = View.GONE
            }
        }
//        刷新数据
        Utils.catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "updateRotation" }.hookAfter {
                LogUtils.i("updateRotation")
                val mResentsContainerRotation = it.args[0] as Int
                val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                if (mResentsContainerRotation == 0) {
                    params.topMargin = (height + 10 + XConfig.getInt("TopMargin0") / 100.0 * heightPixels).toInt()
                    params.leftMargin = (XConfig.getInt("LeftMargin0") / 100.0 * widthPixels).roundToInt()
                } else {
                    params.topMargin = (height + XConfig.getInt("TopMargin1") / 100.0 * widthPixels).roundToInt()
                    params.leftMargin = (10 + XConfig.getInt("LeftMargin1") / 100.0 * heightPixels).roundToInt()
                }
                mLinearLayout.layoutParams = params
                val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
                val swapInfo = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
                val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
//                val a= Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)

//                status color
                TextViewMaps.forEach { (name, view) ->
                    when (name) {
                        "MemoryView" -> {
                            view.text = "运存 ${memoryInfo.availMem.formatSize()} | ${memoryInfo.totalMem.formatSize()} 剩余 ${memoryInfo.percentValue}%"
                            Utils.viewColor(view, memoryInfo)
                        }

                        "ZarmView" -> {
                            view.text = "虚拟 ${swapInfo.availMem.formatSize()} | ${swapInfo.totalMem.formatSize()} 剩余 ${swapInfo.percentValue}%"
                            Utils.viewColor(view, swapInfo)

                        }

                        "StorageView" -> {
                            view.text = "存储 ${storageInfo.availMem.formatSize()} | ${storageInfo.totalMem.formatSize()} 剩余 ${storageInfo.percentValue}%"
                            Utils.viewColor(view, storageInfo)

                        }

                        "BootTime" -> {
                            view.text = "已开机时长 ${Utils.BootTime.get()}"
                        }

                        "RunningAppTotal" -> {
                            view.text = "运行中应用总数 ${(appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses.size}"
                        }

                        "RunningServiceTotal" -> {
                            view.text = "运行中服务总数 ${(appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(999).size}"
                        }

                    }
                    view.width = view.paint.measureText(view.text.toString()).toInt() + 6
                }


            }
        }
//        广播
        runCatching { appContext.unregisterReceiver(moduleReceiver) }
        appContext.registerReceiver(moduleReceiver, IntentFilter().apply { addAction("MIUIHOME_Server") })
    }

    class ModuleReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Utils.catchNoClass {
                when (intent.getStringExtra("Type")) {
                    "copy_font" -> {
                        LogUtils.i("自定义字体")
                        val path = intent.getStringExtra("Font_Path")
                        if (path.isNullOrEmpty()) return@catchNoClass
                        val file = File(appContext.filesDir.path + "/font")
                        if (file.exists() && file.canWrite()) {
                            file.delete()
                        }
                        val error = FileUtils(appContext).copyFile(File(path), appContext.filesDir.path, "font")
                        if (error.isEmpty()) {
                            TextViewMaps.forEach { (_, view) ->
                                view.typeface = Typeface.createFromFile(appContext.filesDir.path + "/font")
                            }
                            LogUtils.i("自定义字体成功")
                            appContext.sendBroadcast(Intent().apply {
                                action = "MIUIHOME_App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("CopyFont", true)
                            })
                        } else {
                            runCatching {
                                val file1 = File(appContext.filesDir.path + "/font")
                                if (file1.exists() && file1.canWrite()) {
                                    file1.delete()
                                    LogUtils.i("自定义字体失败")
                                }
                            }
                            appContext.sendBroadcast(Intent().apply {
                                action = "MIUIHOME_App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("font_error", error)
                            })
                        }
                    }

                    "delete_font" -> {
                        LogUtils.i("恢复字体")
                        var isOK = false
                        val file = File(appContext.filesDir.path + "/font")
                        if (file.exists() && file.canWrite()) {
                            isOK = file.delete()
                        }
                        TextViewMaps.forEach { (_, view) ->
                            view.typeface = Typeface.createFromFile("")
                        }
                        appContext.sendBroadcast(Intent().apply {
                            action = "MIUIHOME_App_Server"
                            putExtra("app_Type", "DeleteFont")
                            putExtra("DeleteFont", isOK)
                        })
                    }
                }
            }
        }
    }
}