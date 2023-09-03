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
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit.initHandleLoadPackage
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit.setEzClassLoader
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.Log.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val PACKAGE_MIUI_HOME = "com.miui.home"
private const val PACKAGE_POCO_HOME = "com.mi.android.globallauncher"
val homeList = arrayOf(PACKAGE_POCO_HOME, PACKAGE_MIUI_HOME)

private const val PACKAGE_SystemUi = "com.android.systemui"
private const val PACKAGE_Android = "android"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private var isInit: Boolean = true
    var context: Context? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XConfig.getBoolean("MainSwitch")) {
            LogUtils.i("总开关未打开")
            return
        }
        initHandleLoadPackage(lpparam)
        setEzClassLoader(lpparam.classLoader)
        when (lpparam.packageName) {
            in homeList -> Application::class.java.hookBeforeMethod("attach", Context::class.java) {
                EzXHelperInit.apply {
                    initAppContext(it.args[0] as Context)
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
                            if (XConfig.getBoolean("HideAppIcon")) {
                                HideAppIcon.init()
                            }
                            if (XConfig.getBoolean("UnlockHotseat")) {
                                UnlockHotseatIcon.init()
                            }
                            if (XConfig.getBoolean("ShortcutSmallWindow")) {
                                ShortcutSmallWindow.init()
                            }
                            if (XConfig.getBoolean("HideSmallWindow")) {
                                HideSmallWindow.init()
                            }
                            if (XConfig.getBoolean("RemoveSmallWindowRestriction")) {
                                RemoveSmallWindowRestriction2.init()
                            }
                            if (XConfig.getBoolean("UnlockNavType")) {
                                UnlockNavType.init()
                            }
                            Info.init()
                            HideStatusBarWhenEnterResents.init()
                            isInit = false
                            LogUtils.i(InitFields.moduleRes.getString(R.string.HookSuccess))
                        }
                    }.logexIfThrow(InitFields.moduleRes.getString(R.string.HookFailed))
                }
            }

            PACKAGE_SystemUi -> {
                if (XConfig.getBoolean("RemoveSmallWindowRestriction")) {
                    RemoveSmallWindowRestriction3.init()
                }
            }

            PACKAGE_Android -> {
                if (XConfig.getBoolean("RemoveSmallWindowRestriction")) {
                    RemoveSmallWindowRestriction1.init()
                }
            }

            else -> return
        }

    }


    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

}
