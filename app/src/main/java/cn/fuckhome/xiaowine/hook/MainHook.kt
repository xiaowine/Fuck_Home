package cn.fuckhome.xiaowine.hook

import android.app.Application
import android.content.Context
import android.util.Log
import cn.fuckhome.xiaowine.hook.module.AddInfo
import cn.fuckhome.xiaowine.hook.module.ModifyShortcutItemCount
import cn.fuckhome.xiaowine.hook.module.ModifyUnlockGrids
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import cn.fuckhome.xiaowine.utils.hookBeforeMethod
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val PACKAGE_MIUI_HOME = "com.miui.home"


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private var isInit: Boolean = true

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XConfig.getBoolean("MainSwitch")) {
            LogUtils.i("总开关未打开")
            return
        }
        when (lpparam.packageName) {
            PACKAGE_MIUI_HOME -> Application::class.java.hookBeforeMethod("attach", Context::class.java) {
                EzXHelperInit.apply {
                    initHandleLoadPackage(lpparam)
                    initAppContext(it.args[0] as Context)
                    runCatching {
                        if (isInit) {
                            if (XConfig.getBoolean("Pad")) {
                                Utils.catchNoClass {
                                    findMethod("com.miui.home.launcher.common.Utilities") { name == "isPadDevice" }.hookBefore {
                                        Log.i("LSPosed", "isPadDevice")
                                        it.result = true
                                    }
                                }
                            }
                            if (XConfig.getBoolean("Shortcuts")) {
                                ModifyShortcutItemCount.init()
                            }
                            if (XConfig.getBoolean("UnlockGrids")) {
                                ModifyUnlockGrids.init()
                            }
                            AddInfo.init()
                            isInit = false
                            LogUtils.i("Inited hook")
                        }
                    }.logexIfThrow("Failed init hook")
                }
            }

            "com.android.settings" -> {}
            else -> return
        }

    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

}
