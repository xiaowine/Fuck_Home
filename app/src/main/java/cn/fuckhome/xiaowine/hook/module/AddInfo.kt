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
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.FileUtils
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.MemoryUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import cn.fuckhome.xiaowine.utils.Utils.formatSize
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.moduleRes
import com.github.kyuubiran.ezxhelper.utils.*
import java.io.File
import kotlin.math.roundToInt


@SuppressLint("StaticFieldLeak")
object AddInfo : BaseHook() {


    lateinit var textColors: ColorStateList
    private var TextViewMaps = LinkedHashMap<String, TextView>()
    private var TextViewList = arrayListOf<String>()
    private lateinit var mLinearLayout: LinearLayout

    private val metrics = appContext.resources.displayMetrics
    private val widthPixels = metrics.widthPixels
    private val heightPixels = metrics.heightPixels
    private var topMargin = 0
    private var leftMargin = 0

    const val threshold = 20

    private val moduleReceiver by lazy { ModuleReceiver() }


    override fun init() {

//        初始化根控件
        findConstructor("com.miui.home.recents.views.RecentsContainer") { parameterCount == 2 }.hookAfter {
            LogUtils.i(moduleRes.getString(R.string.InitRootView))
            val mView = it.thisObject as FrameLayout
            mLinearLayout = LinearLayout(appContext).apply {
                orientation = LinearLayout.VERTICAL
                if (XConfig.getBoolean("optimizeAnimation")) {
                    visibility = View.GONE
                }
            }
            mView.addView(mLinearLayout)
            listOf("MemoryView", "ZarmView", "StorageView", "BootTime", "RunningAppTotal", "RunningServiceTotal").forEach { s ->
                if (XConfig.getBoolean(s)) {
                    TextViewList.add(s)
                }
            }
        }

//        初始化添加控件
        Utils.catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "onFinishInflate" }.hookAfter {
                LogUtils.i(moduleRes.getString(R.string.InitAddView))
                val mTxtMemoryViewGroup = it.thisObject.getObjectAs<ViewGroup>("mTxtMemoryContainer")
                it.thisObject.putObject("mSeparatorForMemoryInfo", View(appContext))
                for (i in 0 until mTxtMemoryViewGroup.childCount) {
                    mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                }
                val mTxtMemoryInfo1 = it.thisObject.getObjectAs<TextView>("mTxtMemoryInfo1")
                textColors = mTxtMemoryInfo1.textColors

                TextViewMaps.apply {
                    TextViewList.forEach { name ->
                        val view = TextView(appContext).apply {
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

//        调节边距
        Utils.catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "updateRotation" }.hookAfter {
                val mResentsContainerRotation = it.args[0] as Int
                if (mResentsContainerRotation == 0) {
                    topMargin = (10 + XConfig.getInt("TopMargin0", 4) / 100.0 * heightPixels).toInt()
                    leftMargin = (10 + XConfig.getInt("LeftMargin0") / 100.0 * widthPixels).roundToInt()
                } else {
                    topMargin = (10 + XConfig.getInt("TopMargin1", 5) / 100.0 * widthPixels).roundToInt()
                    leftMargin = (10 + XConfig.getInt("LeftMargin1") / 100.0 * heightPixels).roundToInt()
                }
            }
        }

//        动态隐藏以优化动画 刷新数据
        findMethod("com.miui.home.recents.views.RecentsContainer") { name == "startRecentsContainerFadeInAnim" }.hookAfter {
            LogUtils.i(moduleRes.getString(R.string.VisibleView))
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            params.topMargin = topMargin
            params.leftMargin = leftMargin
            mLinearLayout.layoutParams = params

            val memoryInfo = MemoryUtils().getMemoryInfo(appContext)
            val swapInfo = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
            val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())


            LogUtils.i(moduleRes.getString(R.string.UpdateView))
            TextViewMaps.forEach { (name, view) ->
                when (name) {
                    "MemoryView" -> {
                        view.text = moduleRes.getString(R.string.MemoryView).format(memoryInfo.availMem.formatSize(), memoryInfo.totalMem.formatSize(), memoryInfo.percentValue)
                        Utils.viewColor(view, memoryInfo)
                    }

                    "ZarmView" -> {
                        view.text = moduleRes.getString(R.string.ZarmView).format(swapInfo.availMem.formatSize(), swapInfo.totalMem.formatSize(), swapInfo.percentValue)
                        Utils.viewColor(view, swapInfo)
                    }

                    "StorageView" -> {
                        view.text = moduleRes.getString(R.string.StorageView).format(storageInfo.availMem.formatSize(), storageInfo.totalMem.formatSize(), storageInfo.percentValue)
                        Utils.viewColor(view, storageInfo)
                    }

                    "BootTime" -> {

                        view.text = moduleRes.getString(R.string.BootTimeView).format(Utils.BootTime.get())
                    }

                    "RunningAppTotal" -> {
                        view.text = moduleRes.getString(R.string.RunningAppTotalView).format((appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses.size)
                    }

                    "RunningServiceTotal" -> {
                        view.text = moduleRes.getString(R.string.RunningServiceTotalView).format((appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(999).size)
                    }

                }
                view.width = view.paint.measureText(view.text.toString()).toInt() + 6
            }

            val animation = AlphaAnimation(0f, 1f)
            animation.duration = 300
            mLinearLayout.startAnimation(animation)
            mLinearLayout.visibility = View.VISIBLE
        }

        findMethod("com.miui.home.recents.views.RecentsContainer") { name == "startRecentsContainerFadeOutAnim" }.hookAfter {
            LogUtils.i(moduleRes.getString(R.string.GoneView))
            if (mLinearLayout.visibility != View.GONE) {
                val animation = AlphaAnimation(1f, 0f)
                animation.duration = 300
                mLinearLayout.startAnimation(animation)
                mLinearLayout.visibility = View.GONE
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
                        LogUtils.i(moduleRes.getString(R.string.CustomFont))
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
                            LogUtils.i(moduleRes.getString(R.string.CustomFontSuccess))
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
                                    LogUtils.i(moduleRes.getString(R.string.CustomFontFail))
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
                        LogUtils.i(moduleRes.getString(R.string.DeleteFont))
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