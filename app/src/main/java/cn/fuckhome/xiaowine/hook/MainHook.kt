package cn.fuckhome.xiaowine.hook

import android.app.Application
import android.content.Context
import cn.fuckhome.xiaowine.R
import cn.fuckhome.xiaowine.hook.module.add.Info
import cn.fuckhome.xiaowine.hook.module.modify.*
import cn.fuckhome.xiaowine.utils.LogUtils
import cn.fuckhome.xiaowine.utils.Utils.XConfig
import cn.fuckhome.xiaowine.utils.hookBeforeMethod
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val PACKAGE_MIUI_HOME = "com.miui.home"


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private var isInit: Boolean = true
    var context: Context? = null

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
                    setEzClassLoader(InitFields.appContext.classLoader)
                    runCatching {
                        if (isInit) {
                            if (XConfig.getBoolean("Pad")) {
                                UnlockPad.init()
                            }
                            if (XConfig.getBoolean("Shortcuts")) {
                                ShortcutItemCount.init()
                            }
                            if (XConfig.getBoolean("UnlockGrids")) {
                                UnlockGrids.init()
                            }
                            if (XConfig.getBoolean("HideAppName")) {
                                HideAppName.init()
                            }
                            Info.init()
                            HideStatusBarWhenEnterResents.init()
                            isInit = false
                            LogUtils.i(InitFields.moduleRes.getString(R.string.HookSuccess))
                        }
                    }.logexIfThrow(InitFields.moduleRes.getString(R.string.HookFailed))
                }
            }

            else -> return
        }

    }


    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

}
