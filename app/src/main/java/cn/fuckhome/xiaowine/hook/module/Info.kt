@file:Suppress("DEPRECATION")

package cn.fuckhome.xiaowine.hook.module

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import cn.fuckhome.xiaowine.hook.BaseHook
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.MemoryUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.formatSize
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.*
import kotlin.math.roundToInt

@SuppressLint("StaticFieldLeak")
object Info : BaseHook() {


    lateinit var textColors: ColorStateList
    private var height: Int = 0
    private var TextViewMaps = LinkedHashMap<String, TextView>()
    private var TextViewList = arrayListOf<String>()
    private lateinit var mLinearLayout: LinearLayout

    private val metrics = InitFields.appContext.resources.displayMetrics
    private val widthPixels = metrics.widthPixels
    private val heightPixels = metrics.heightPixels

    const val threshold = 50

    @SuppressLint("SetTextI18n")
    override fun init() {
        val resourceId: Int = InitFields.appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        height = InitFields.appContext.resources.getDimensionPixelSize(resourceId)
//        初始化根控件
        findConstructor("com.miui.home.recents.views.RecentsContainer") { parameterCount == 2 }.hookAfter {
            val mView = it.thisObject as FrameLayout
            mLinearLayout = LinearLayout(InitFields.appContext).apply {
                orientation = LinearLayout.VERTICAL
            }
            mView.addView(mLinearLayout)
            listOf("MemoryView", "ZarmView", "StorageView", "BootTime", "RunningAppTotal", "RunningServiceTotal").forEach { s ->
                LogUtils.i(Utils.XConfig.getBoolean(s))
                if (Utils.XConfig.getBoolean(s)) {
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
                it.thisObject.putObject("mSeparatorForMemoryInfo", View(InitFields.appContext))
                for (i in 0 until mTxtMemoryViewGroup.childCount) {
                    mTxtMemoryViewGroup.getChildAt(i).visibility = View.GONE
                }
                val mTxtMemoryInfo1 = it.thisObject.getObjectAs<TextView>("mTxtMemoryInfo1")
                textColors = mTxtMemoryInfo1.textColors

                TextViewMaps.apply {
                    TextViewList.forEach { name ->
                        val view = TextView(InitFields.appContext).apply {
                            LogUtils.i("Init view $name")
                            setBackgroundColor(Color.parseColor(Utils.XConfig.getBgColor()))
                            Utils.viewColor(this, null)
                            gravity = Utils.XConfig.getGravity()
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

//        刷新数据
        Utils.catchNoClass {
            findMethod("com.miui.home.recents.views.RecentsContainer") { name == "updateRotation" }.hookAfter {
                LogUtils.i("updateRotation")
                val mResentsContainerRotation = it.args[0] as Int

                val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                if (mResentsContainerRotation == 0) {
                    params.topMargin = (height + 10 + Utils.XConfig.getInt("TopMargin0") / 100.0 * heightPixels).toInt()
                    params.leftMargin = (Utils.XConfig.getInt("LeftMargin0") / 100.0 * widthPixels).roundToInt()
                } else {
                    params.topMargin = (height + Utils.XConfig.getInt("TopMargin1") / 100.0 * widthPixels).roundToInt()
                    params.leftMargin = (10 + Utils.XConfig.getInt("LeftMargin1") / 100.0 * heightPixels).roundToInt()
                }
                mLinearLayout.layoutParams = params
                val memoryInfo = MemoryUtils().getMemoryInfo(InitFields.appContext)
                val swapInfo = MemoryUtils().getPartitionInfo("SwapTotal", "SwapFree")
                val storageInfo = MemoryUtils().getStorageInfo(Environment.getExternalStorageDirectory())
//                val a= Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)

//                status color
                TextViewMaps.forEach { (name, view) ->
                    run {
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
                                view.text = "运行中应用总数 ${(InitFields.appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses.size}"
                            }

                            "RunningServiceTotal" -> {
                                view.text = "运行中服务总数 ${(InitFields.appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(999).size}"
                            }

                        }
                        view.width = view.paint.measureText(view.text.toString()).toInt() + 6
                    }
                }
            }
        }

    }
}